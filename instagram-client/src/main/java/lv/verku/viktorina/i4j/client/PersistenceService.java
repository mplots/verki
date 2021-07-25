package lv.verku.viktorina.i4j.client;

import com.github.instagram4j.instagram4j.models.media.reel.VoterInfo;
import lombok.AllArgsConstructor;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.i4j.model.*;
import lv.verku.viktorina.jdbc.dao.*;
import lv.verku.viktorina.jdbc.dto.*;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class PersistenceService {

    private Properties properties;
    private PoolDao poolDao;
    private PoolTallyDao poolTallyDao;
    private ProfileDao profileDao;
    private VoterDao voterDao;
    private HashtagDao hashtagDao;
    private PoolHashtagDao poolHashtagDao;
    private QuizDao quizDao;
    private QuizTallyDao quizTallyDao;
    private AnswerDao answerDao;
    private QuizHashtagDao quizHashtagDao;

    public void persist(List<ReelMediaWrapper> reelMedia) {
        for (ReelMediaWrapper media : reelMedia) {

           Profile owner = persistProfile(media.getOwner());
           ReelExtraProperties properties = media.getReelExtraProperties();

            for (StoryPool storyPool : properties.getStoryPools()) {
                StoryPoolSticker sticker = storyPool.getPoolSticker();
                Pool pool = persistPool(owner, sticker);

                List<PoolTally> tallies = new ArrayList<>();
                for (StoryPoolTally storyPoolTally : sticker.getTallies()) {
                    PoolTally tally = persistPoolTally(pool, storyPoolTally);
                    tallies.add(tally);
                }

                for (VoterInfo voterInfo : properties.getStoryPollVoterInfos()) {
                    for (VoterInfo.Voter voterItem : voterInfo.getVoters()) {
                        Profile profile = persistProfile(voterItem.getUser());
                        persistVoter(profile, voterItem, tallies);
                    }
                }
            }

            for (StoryQuiz storyQuiz: properties.getStoryQuizs()) {
                StoryQuizSticker sticker = storyQuiz.getQuizSticker();
                Quiz quiz = persistQuiz(owner, sticker);

                List<QuizTally> tallies = new ArrayList<>();
                for (StoryQuizTally storyQuizTally : sticker.getTallies()) {
                    QuizTally quizTally = persistQuizTally(quiz, storyQuizTally);
                    tallies.add(quizTally);
                }
                quiz.setCorrectTallyId(tallies.get(sticker.getCorrectAnswer()).getId());
                quizDao.upsert(quiz);

                for(StoryParticipantInfo participantInfo : properties.getStoryQuizParticipantInfos()) {
                    for (StoryQuizParticipant participant : participantInfo.getParticipants()) {
                        Profile profile = persistProfile(participant.getUser());
                        persistParticipant(profile, participant, tallies);
                    }
                }
            }

            for (StoryHashtagsItem storyHashtag : properties.getStoryHashtags()) {
                Hashtag hashtag = persistHashtag(storyHashtag);

                for (StoryPool storyPool : properties.getStoryPools()) {
                    StoryPoolSticker sticker = storyPool.getPoolSticker();
                    persistPoolHashtag(sticker, hashtag);
                }

                for (StoryQuiz storyQuiz : properties.getStoryQuizs()) {
                    StoryQuizSticker sticker = storyQuiz.getQuizSticker();
                    persistQuizHashtag(sticker, hashtag);
                }
            }
        }
    }

    private Quiz persistQuiz(Profile owner, StoryQuizSticker quizSticker) {
        Quiz quiz = Quiz.builder()
                .id(quizSticker.getQuizId())
                .question(quizSticker.getQuestion())
                .ownerId(owner.getId()).build();

        quizDao.upsert(quiz);
        return quiz;
    }

    private QuizTally persistQuizTally(Quiz quiz, StoryQuizTally storyQuizTally) {
        QuizTally quizTally = QuizTally.builder()
                .text(storyQuizTally.getText())
                .quizId(quiz.getId())
                .build();

        quizTallyDao.upsert(quizTally);
        return quizTally;
    }

    private Answer persistParticipant(Profile profile, StoryQuizParticipant participant, List<QuizTally> tallies) {
        Answer answer = Answer.builder()
                .profileId(profile.getId())
                .quizTallyId(tallies.get(participant.getAnswer()).getId())
                .build();
        answerDao.upsert(answer);
        return answer;
    }

    private QuizHashtag persistQuizHashtag(StoryQuizSticker storyQuizSticker, Hashtag hashtag) {
        QuizHashtag quizHashtag = QuizHashtag.builder()
                .hashtagId(hashtag.getId())
                .quizId(storyQuizSticker.getQuizId()).build();
        quizHashtagDao.upsert(quizHashtag);
        return quizHashtag;
    }

    private Pool persistPool(Profile owner, StoryPoolSticker poolSticker) {
        Pool pool = Pool.builder()
                .id(poolSticker.getPollId())
                .question(poolSticker.getQuestion())
                .ownerId(owner.getId())
                .build();
        poolDao.upsert(pool);
        return pool;
    }

    private PoolTally persistPoolTally(Pool pool, StoryPoolTally storyPoolTally) {
        PoolTally tally = PoolTally.builder()
                .text(storyPoolTally.getText())
                .poolId(pool.getId())
                .build();
        poolTallyDao.upsert(tally);
        return tally;
    }

    private Profile persistProfile(com.github.instagram4j.instagram4j.models.user.Profile user) {

        Profile profile = Profile.builder()
                .id(user.getPk())
                .fullName(user.getFull_name())
                .username(user.getUsername())
                .pictureUrl(user.getProfile_pic_url())
                .build();
        profileDao.upsert(profile);
        return profile;
    }

    private Voter persistVoter(Profile profile, VoterInfo.Voter voter, List<PoolTally> tallies) {
        Voter vote = Voter.builder()
                .poolTallyId(tallies.get(voter.getVote()).getId())
                .profileId(profile.getId())
                .build();
        voterDao.upsert(vote);
        return vote;
    }

    private Hashtag persistHashtag(StoryHashtagsItem storyHashtagsItem) {
        Hashtag hashtag = Hashtag.builder()
                .id(storyHashtagsItem.getHashtag().getId())
                .hashtag(storyHashtagsItem.getHashtag().getName())
                .build();
        hashtagDao.upsert(hashtag);
        return hashtag;
    }

    private PoolHashtag persistPoolHashtag(StoryPoolSticker storyPoolSticker, Hashtag hashtag) {
        PoolHashtag poolHashtag = PoolHashtag.builder()
                .hashtagId(hashtag.getId())
                .poolId(storyPoolSticker.getPollId()).build();

        poolHashtagDao.upsert(poolHashtag);
        return poolHashtag;
    }
}

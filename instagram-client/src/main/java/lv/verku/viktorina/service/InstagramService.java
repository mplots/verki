package lv.verku.viktorina.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.i4j.client.Client;
import lv.verku.viktorina.i4j.client.PersistenceService;
import lv.verku.viktorina.i4j.model.PublicProfile;
import lv.verku.viktorina.i4j.model.ReelMediaWrapper;
import lv.verku.viktorina.jdbc.dao.DuelDao;
import lv.verku.viktorina.jdbc.dao.PoolTallyDao;
import lv.verku.viktorina.jdbc.dao.ProfileDao;
import lv.verku.viktorina.jdbc.dao.QuizSeriesLeaderboardDao;
import lv.verku.viktorina.jdbc.dto.DuelParticipant;
import lv.verku.viktorina.jdbc.dto.PoolTally;
import lv.verku.viktorina.jdbc.dto.Profile;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;
import lv.verku.viktorina.service.exception.UnexpectedNumberOfPoolsForHashtagException;
import lv.verku.viktorina.service.model.Leaderboard;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static lv.verku.viktorina.jdbc.dto.Profile.MALE_GENDER_ID;
import static lv.verku.viktorina.jdbc.dto.Profile.FEMALE_GENDER_ID;
import static lv.verku.viktorina.jdbc.dto.Profile.UNKNOWN_GENDER_ID;

@Component
@AllArgsConstructor
public class InstagramService {

    private Properties properties;
    private Client client;
    private PersistenceService persistenceService;
    private QuizSeriesLeaderboardDao quizSeriesLeaderboardDao;
    private DuelDao duelDao;
    private PoolTallyDao poolTallyDao;
    private ProfileDao profileDao;

    public void pull() {
        if (!properties.getDisablePool()) {
            List<ReelMediaWrapper> media = client.getReelMedia();
            persistenceService.persist(media);
        }
    }

    public Leaderboard getQuizSeriesLeaderboard(List<String> hastags) {
        List<QuizSeriesParticipant> leaders = quizSeriesLeaderboardDao.get(hastags);
        Map<Long, List<QuizSeriesParticipant>> groupedLeaders = leaders.stream().collect(Collectors.groupingBy(QuizSeriesParticipant::getPlace));

        return Leaderboard.builder().
                leaders(groupedLeaders).
                maleCount(countGenderLeaders(leaders, MALE_GENDER_ID)).
                femaleCount(countGenderLeaders(leaders, FEMALE_GENDER_ID)).
                unknownCount(countGenderLeaders(leaders, UNKNOWN_GENDER_ID)).
               build();
    }

    public Map<String, List<DuelParticipant>> getDuel(String hashtag) {
        List<PoolTally> poolTallies = poolTallyDao.getForHashtag(hashtag);
        if (poolTallies.size() != 2) {
            throw new UnexpectedNumberOfPoolsForHashtagException();
        }

        //Set default empty values
        Map<String, List<DuelParticipant>> result = new TreeMap<>();
        result.put(poolTallies.get(0).getText(), new ArrayList<>());
        result.put(poolTallies.get(1).getText(), new ArrayList<>());
        
        Map<String, List<DuelParticipant>> duel = duelDao.get(hashtag).stream().collect(Collectors.groupingBy(DuelParticipant::getAnswer));
        for (String key : duel.keySet()) {
            result.put(key, duel.get(key));
        }
        
        return result;
    }

    public void synchronizeProfilePictures() {
        List<Profile> profiles =  profileDao.getAll();
        for (Profile profile: profiles) {
            PublicProfile publicProfile = client.getPublicProfile(profile.getUsername());
            profile.setPictureUrl(publicProfile.getGraphql().getUser().getProfilePicUrl());
            System.out.println(profile.getUsername());
            profileDao.upsert(profile);
        }
    }

    private Long countGenderLeaders(List<QuizSeriesParticipant> leaders, Long gender) {
        return leaders.stream().filter(
                e-> e.getGenderId() == gender
        ).mapToLong(e->e.getCorrectAnswers()).sum();
    }
}

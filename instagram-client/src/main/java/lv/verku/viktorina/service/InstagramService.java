package lv.verku.viktorina.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.i4j.client.Client;
import lv.verku.viktorina.i4j.client.PersistenceService;
import lv.verku.viktorina.i4j.client.exception.ClientException;
import lv.verku.viktorina.i4j.model.PublicProfile;
import lv.verku.viktorina.i4j.model.ReelMediaWrapper;
import lv.verku.viktorina.jdbc.dao.*;
import lv.verku.viktorina.jdbc.dto.DuelParticipant;
import lv.verku.viktorina.jdbc.dto.PoolTally;
import lv.verku.viktorina.jdbc.dto.Profile;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;
import lv.verku.viktorina.service.exception.UnexpectedNumberOfPoolsForHashtagException;
import lv.verku.viktorina.service.model.Leaderboard;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static lv.verku.viktorina.jdbc.dto.Profile.MALE_GENDER_ID;
import static lv.verku.viktorina.jdbc.dto.Profile.FEMALE_GENDER_ID;
import static lv.verku.viktorina.jdbc.dto.Profile.UNKNOWN_GENDER_ID;

@Component
@AllArgsConstructor
@Log4j
public class InstagramService {

    private Properties properties;
    private Client client;
    private PersistenceService persistenceService;
    private QuizSeriesLeaderboardDao quizSeriesLeaderboardDao;
    private PoolSeriesLeaderboardDao poolSeriesLeaderboardDao;
    private DuelDao duelDao;
    private PoolTallyDao poolTallyDao;
    private ProfileDao profileDao;

    public void pull() {
        if (!properties.getDisablePool()) {
            List<ReelMediaWrapper> media = client.getReelMedia();
            persistenceService.persist(media);
            downloadMissingProfilePictures();
        }
    }

    public Leaderboard getPoolSeriesLeaderboard(List<String> hastags) {
        return getSeriesLeaderboard(hastags, poolSeriesLeaderboardDao);
    }

    public Leaderboard getQuizSeriesLeaderboard(List<String> hastags) {
        return getSeriesLeaderboard(hastags, quizSeriesLeaderboardDao);
    }

    private Leaderboard getSeriesLeaderboard(List<String> hastags, BaseSeriesLeaderboardDao dao) {
        List<QuizSeriesParticipant> leaders = dao.get(hastags);
        Map<Long, List<QuizSeriesParticipant>> groupedLeaders = leaders.stream().collect(Collectors.groupingBy(QuizSeriesParticipant::getPlace));

        return Leaderboard.builder().
                leaders(groupedLeaders).
                maleCount(countGenderLeaders(leaders, MALE_GENDER_ID)).
                femaleCount(countGenderLeaders(leaders, FEMALE_GENDER_ID)).
                unknownCount(countGenderLeaders(leaders, UNKNOWN_GENDER_ID)).
                currentTimeMillis(System.currentTimeMillis()).
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

    @Async("profilePictureSynchronizeTaskExecutor")
    public void synchronizeProfilePictures() {
        List<Profile> profiles =  profileDao.getWithMissingPictures();
        for (Profile profile: profiles) {
            try {
                PublicProfile publicProfile = client.getPublicProfile(profile.getUsername());
                if (publicProfile.getGraphql() != null
                        && publicProfile.getGraphql().getUser() != null
                        && publicProfile.getGraphql().getUser().getProfilePicUrl() != null
                ) {
                    profile.setPictureUrl(publicProfile.getGraphql().getUser().getProfilePicUrl());
                    String imagePath = properties.getImageDirectory() + profile.getUsername();
                    Boolean downloaded = client.downloadProfilePicture(profile.getPictureUrl(), imagePath, true);
                    if (downloaded) {
                        profile.setPictureDownloadTime(LocalDateTime.now());
                        profileDao.upsert(profile);
                        log.info("Profile " + profile.getUsername() + " photo synchronized.");
                    }
                }
            }catch (ClientException e) {
                log.error("Failed to synchronize profile " + profile.getUsername() + " photo. ", e);
            }
        }
    }

    @Async("profilePictureDownloadTaskExecutor")
    public void downloadMissingProfilePictures() {
        List<Profile> profiles =  profileDao.getWithMissingPictures();
        for (Profile profile: profiles) {
            try {
                String imagePath = properties.getImageDirectory() + profile.getUsername();
                Boolean downloaded = client.downloadProfilePicture(profile.getPictureUrl(), imagePath, true);
                if (downloaded) {
                    profile.setPictureDownloadTime(LocalDateTime.now());
                    profileDao.upsert(profile);
                    log.info("Profile " + profile.getUsername() + " photo downloaded.");
                }
            }catch (ClientException e) {
                log.error("Failed to download profile " + profile.getUsername() + " photo. ", e);
            }
        }
    }

    private Long countGenderLeaders(List<QuizSeriesParticipant> leaders, Long gender) {
        return leaders.stream().filter(
                e-> e.getGenderId() == gender
        ).mapToLong(e->e.getCorrectAnswers()).sum();
    }
}

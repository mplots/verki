package lv.verku.viktorina.service;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.i4j.client.Client;
import lv.verku.viktorina.i4j.client.PersistenceService;
import lv.verku.viktorina.i4j.model.ReelMediaWrapper;
import lv.verku.viktorina.jdbc.dao.DuelDao;
import lv.verku.viktorina.jdbc.dao.PoolTallyDao;
import lv.verku.viktorina.jdbc.dao.QuizSeriesLeaderboardDao;
import lv.verku.viktorina.jdbc.dto.DuelParticipant;
import lv.verku.viktorina.jdbc.dto.PoolTally;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;
import lv.verku.viktorina.service.exception.UnexpectedNumberOfPoolsForHashtagException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InstagramService {

    private Properties properties;
    private Client client;
    private PersistenceService persistenceService;
    private QuizSeriesLeaderboardDao quizSeriesLeaderboardDao;
    private DuelDao duelDao;
    private PoolTallyDao poolTallyDao;

    public void pull() {
        if (!properties.getDisablePool()) {
            List<ReelMediaWrapper> media = client.getReelMedia();
            persistenceService.persist(media);
        }
    }

    public Map<Long, List<QuizSeriesParticipant>> getQuizSeriesLeaderboard(List<String> hastags) {
        List<QuizSeriesParticipant> leaders = quizSeriesLeaderboardDao.get(hastags);
        return leaders.stream().collect(Collectors.groupingBy(QuizSeriesParticipant::getPlace));
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
}

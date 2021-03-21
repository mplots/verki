package lv.verku.viktorina.i4j.client;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.i4j.model.ReelMediaWrapper;
import lv.verku.viktorina.jdbc.dao.QuizSeriesLeaderboardDao;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InstagramService {

    private Properties properties;
    private Client client;
    private PersistenceService persistenceService;
    private QuizSeriesLeaderboardDao quizSeriesLeaderboardDao;

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
}

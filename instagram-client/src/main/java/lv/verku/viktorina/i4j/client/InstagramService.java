package lv.verku.viktorina.i4j.client;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.i4j.model.ReelMediaWrapper;
import lv.verku.viktorina.jdbc.dao.QuizSeriesLeaderboardDao;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class InstagramService {

    private Client client;
    private PersistenceService persistenceService;
    private QuizSeriesLeaderboardDao quizSeriesLeaderboardDao;

    public void pull() {
        List<ReelMediaWrapper> media = client.getReelMedia();
        persistenceService.persist(media);
    }

    public List<QuizSeriesParticipant> getQuizSeriesLeaderboard(List<String> hastags) {
        return quizSeriesLeaderboardDao.get(hastags);
    }
}

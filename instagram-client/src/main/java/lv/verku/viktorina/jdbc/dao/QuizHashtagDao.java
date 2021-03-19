package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.QuizHashtag;
import org.springframework.stereotype.Repository;

@Repository
public class QuizHashtagDao extends BaseDao<QuizHashtag> {

    @Override
    public void upsert(QuizHashtag quizHashtag) {
        jdbcTemplate.update(
                "INSERT INTO quiz_hashtag (quiz_id, hashtag_id) VALUES (?,?)" +
                        "ON CONFLICT (quiz_id, hashtag_id) DO NOTHING;",
                 quizHashtag.getQuizId(), quizHashtag.getHashtagId()
        );
    }
}

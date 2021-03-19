package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.Quiz;
import org.springframework.stereotype.Repository;

@Repository
public class QuizDao extends BaseDao<Quiz> {
    public void upsert(Quiz quiz) {
        jdbcTemplate.update(
        "INSERT INTO quiz (id,question,correct_tally_id,owner_id) VALUES (?,?,?,?)" +
            "ON CONFLICT (id) DO " +
            "UPDATE SET question = excluded.question, correct_tally_id=excluded.correct_tally_id, owner_id= excluded.owner_id;",
            quiz.getId(), quiz.getQuestion(), quiz.getCorrectTallyId(), quiz.getOwnerId()
        );
    }

}

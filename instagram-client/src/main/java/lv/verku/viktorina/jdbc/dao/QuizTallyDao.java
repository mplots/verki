package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.QuizTally;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class QuizTallyDao extends BaseDao<QuizTally> {

    public void upsert(QuizTally tally) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO quiz_tally (quiz_id, text) VALUES (?,?) " +
                    "ON CONFLICT (quiz_id, text) DO " +
                    "UPDATE SET quiz_id = excluded.quiz_id, text = excluded.text " +
                    "RETURNING id;",
                 new Object[] {tally.getQuizId(), tally.getText()},
                Long.class
        );

        tally.setId(id);
    }
}

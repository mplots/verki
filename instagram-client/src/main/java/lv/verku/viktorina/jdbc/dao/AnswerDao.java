package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.Answer;
import org.springframework.stereotype.Repository;

@Repository
public class AnswerDao extends BaseDao<Answer>{

    public void upsert(Answer answer) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO answer (profile_id,quiz_tally_id) VALUES (?,?) " +
                    "ON CONFLICT (profile_id, quiz_tally_id) DO " +
                    "UPDATE SET profile_id=excluded.profile_id, quiz_tally_id=excluded.quiz_tally_id " +
                    "RETURNING id;",
               new Object[] {answer.getProfileId(), answer.getQuizTallyId()}, Long.class
        );
        answer.setId(id);
    }
}

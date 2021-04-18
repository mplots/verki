package lv.verku.viktorina.jdbc.dao;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.jdbc.dto.DuelParticipant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class DuelDao {
    protected JdbcTemplate jdbcTemplate;

    public List<DuelParticipant> get(String hashtag) {
        return jdbcTemplate.query(
                "SELECT p.*, pt.text as answer  FROM pool " +
                "JOIN pool_hashtag ph on pool.id = ph.pool_id " +
                "JOIN hashtag h on ph.hashtag_id = h.id " +
                "JOIN pool_tally pt on pool.id = pt.pool_id " +
                "JOIN voter v on pt.id = v.pool_tally_id " +
                "JOIN profile p on v.profile_id = p.id " +
                "WHERE h.hashtag = ?",
                (resultSet, i) ->
                            DuelParticipant.builder().
                                username(resultSet.getString("username")).
                                fullName(resultSet.getString("full_name")).
                                id(resultSet.getLong("id")).
                                answer(resultSet.getString("answer")).
                                pictureUrl(resultSet.getString("picture_url")).
                            build()
                , hashtag);
    }
}

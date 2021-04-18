package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.DuelParticipant;
import lv.verku.viktorina.jdbc.dto.Pool;
import lv.verku.viktorina.jdbc.dto.PoolTally;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PoolTallyDao extends BaseDao<PoolTally> {

    @Override
    public void upsert(PoolTally poolTally) {
        Long id = jdbcTemplate.queryForObject(
        "INSERT INTO pool_tally (pool_id, text) VALUES (?,?) " +
            "ON CONFLICT (pool_id, text) DO " +
            "UPDATE SET pool_id = excluded.pool_id, text = excluded.text" +
            " RETURNING id",
            new Object[] { poolTally.getPoolId(), poolTally.getText() },
            Long.class
        );
        poolTally.setId(id);
    }

    public List<PoolTally> getForHashtag(String hashtag) {
        return jdbcTemplate.query(
                "SELECT pt.* FROM pool_tally pt\n" +
                        "JOIN pool p on p.id = pt.pool_id\n" +
                        "JOIN pool_hashtag ph on p.id = ph.pool_id\n" +
                        "JOIN hashtag h on ph.hashtag_id = h.id\n" +
                        "WHERE h.hashtag = ?",
                (resultSet, i) ->
                        PoolTally.builder().
                                text(resultSet.getString("text")).
                                poolId(resultSet.getLong("id")).
                                build()
                , hashtag);
    }

}

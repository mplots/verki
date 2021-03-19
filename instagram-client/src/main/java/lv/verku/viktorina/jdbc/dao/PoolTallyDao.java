package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.PoolTally;
import org.springframework.stereotype.Repository;

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

}

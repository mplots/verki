package lv.verku.viktorina.jdbc.dao;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.jdbc.dto.Pool;
import org.springframework.stereotype.Repository;

@Repository
public class PoolDao extends BaseDao<Pool>{

    @Override
    public void upsert(Pool pool) {
        jdbcTemplate.update(
                "INSERT INTO pool (id, question, owner_id) VALUES (?,?,?)" +
                        "ON CONFLICT (id) DO " +
                        "UPDATE SET question = excluded.question,owner_id= excluded.owner_id;",
                pool.getId(), pool.getQuestion(), pool.getOwnerId()
        );
    }
}

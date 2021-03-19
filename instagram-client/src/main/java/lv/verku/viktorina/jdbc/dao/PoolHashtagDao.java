package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.PoolHashtag;
import org.springframework.stereotype.Repository;

@Repository
public class PoolHashtagDao extends BaseDao<PoolHashtag> {

    @Override
    public void upsert(PoolHashtag poolHashtag) {
        jdbcTemplate.update(
        "INSERT INTO pool_hashtag (pool_id, hashtag_id) VALUES (?,?)" +
            "ON CONFLICT (pool_id, hashtag_id) DO NOTHING;",
            poolHashtag.getPoolId(), poolHashtag.getHashtagId()
        );
    }

}

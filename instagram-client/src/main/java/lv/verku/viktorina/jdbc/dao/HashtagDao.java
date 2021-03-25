package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.Hashtag;
import org.springframework.stereotype.Repository;

@Repository
public class HashtagDao extends BaseDao<Hashtag> {

    public void upsert(Hashtag hashtag) {
        jdbcTemplate.update(
        "INSERT INTO hashtag (id, hashtag) VALUES (?,?)" +
            "ON CONFLICT (id) DO NOTHING;",
            hashtag.getId(), hashtag.getHashtag()
        );
    }
}

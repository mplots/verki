package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.Profile;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileDao extends BaseDao<Profile>{

    public void upsert(Profile profile) {
        jdbcTemplate.update(
                "INSERT INTO profile (id, username, full_name, picture_url) VALUES (?,?,?,?) " +
                        "ON CONFLICT (id) DO " +
                        "UPDATE SET id = excluded.id, username = excluded.username, full_name = excluded.full_name, picture_url=excluded.picture_url ;",
                profile.getId(), profile.getUsername(), profile.getFullName(), profile.getPictureUrl()
        );
    }

}
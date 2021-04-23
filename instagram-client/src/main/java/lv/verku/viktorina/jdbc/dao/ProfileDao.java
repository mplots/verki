package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<Profile> getAll() {
        return jdbcTemplate.query("SELECT *FROM profile",
                (resultSet, i) -> Profile.builder().
                        id(resultSet.getLong("id")).
                        username(resultSet.getString("username")).
                        fullName(resultSet.getString("full_name")).
                        pictureUrl(resultSet.getString("picture_url")).
                        build()
        );
    }

}
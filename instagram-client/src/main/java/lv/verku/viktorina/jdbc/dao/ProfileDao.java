package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.Profile;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProfileDao extends BaseDao<Profile>{

    public Profile get(Long id) {
        List<Profile> profiles = jdbcTemplate.query("SELECT *FROM profile p WHERE p.id = ?",
                (resultSet, i) -> buildProfile(resultSet), id);
        if (profiles.size() == 1) {
            return profiles.get(0);
        }
        return null;
    }

    public void upsert(Profile profile) {
        jdbcTemplate.update(
                "INSERT INTO profile AS p (id, username, full_name, picture_url, picture_download_time) VALUES (?,?,?,?,?) " +
                        "ON CONFLICT (id) DO " +
                        "UPDATE SET id = excluded.id, username = excluded.username, " +
                        "full_name = excluded.full_name, " +
                        "picture_url=excluded.picture_url, " +
                        "picture_download_time=COALESCE(excluded.picture_download_time, p.picture_download_time);",
                profile.getId(), profile.getUsername(), profile.getFullName(), profile.getPictureUrl(), toDate(profile.getPictureDownloadTime())
        );
    }

    public List<Profile> getWithMissingPictures() {
        return jdbcTemplate.query("SELECT *FROM profile WHERE picture_download_time IS NULL",
                (resultSet, i) -> buildProfile(resultSet));
    }

    private Profile buildProfile(ResultSet resultSet) throws SQLException {
        return Profile.builder().
                id(resultSet.getLong("id")).
                username(resultSet.getString("username")).
                fullName(resultSet.getString("full_name")).
                pictureUrl(resultSet.getString("picture_url")).
                pictureDownloadTime(fromDate(resultSet.getTimestamp("picture_download_time"))).
                build();
    }
}
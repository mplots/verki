package lv.verku.viktorina.jdbc.dao;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@AllArgsConstructor
public class PoolSeriesLeaderboardDao extends BaseSeriesLeaderboardDao {

    protected JdbcTemplate jdbcTemplate;

    @Override
    public List<QuizSeriesParticipant> get(List<String> hashtags) {

        String inSql = String.join(",", Collections.nCopies(hashtags.size(), "?"));

        return jdbcTemplate.query(
                "WITH aggregated_pool_hastags AS ( " +
                    "    SELECT ph.pool_id, array_agg(hashtag) as hashtags " +
                    "    FROM hashtag h " +
                    "             INNER JOIN pool_hashtag ph on h.id = ph.hashtag_id " +
                    "    GROUP BY ph.pool_id " +
                    "), " +
                    "series AS ( " +
                    "     SELECT *FROM aggregated_pool_hastags WHERE ARRAY["+ inSql +"]::text[] <@ hashtags " +
                    " ), " +
                    " vote_count AS ( " +
                    "    SELECT s.pool_id, pool_tally_id, count(*)::decimal as vote_count FROM voter " +
                    "    JOIN pool_tally pt ON pt.id = voter.pool_tally_id " +
                    "    JOIN pool p ON p.id = pt.pool_id " +
                    "    JOIN series s ON s.pool_id = p.id " +
                    "    GROUP BY s.pool_id, pool_tally_id " +
                    "), " +
                    "pool_vote_count AS ( " +
                    "    SELECT s.pool_id, count(*)::decimal as pool_vote_count FROM voter " +
                    "    JOIN pool_tally pt ON pt.id = voter.pool_tally_id " +
                    "    JOIN pool p ON p.id = pt.pool_id " +
                    "    JOIN series s ON s.pool_id = p.id " +
                    "    GROUP BY s.pool_id " +
                    "), " +
                    "tally_percentage AS ( " +
                        "SELECT vc.pool_tally_id, ROUND(vote_count/pool_vote_count, 2)*100 as percentage  FROM pool_vote_count pvc " +
                        "INNER JOIN vote_count vc ON pvc.pool_id = vc.pool_id " +
                        "ORDER BY vc.pool_id " +
                    ")," +
                    "profile_answers AS ( " +
                        "SELECT p2.id, count(p2.id) as correct_answers FROM tally_percentage tp " +
                        "INNER JOIN voter v ON v.pool_tally_id = tp.pool_tally_id " +
                        "INNER JOIN profile p2 on p2.id = v.profile_id " +
                        "WHERE tp.percentage >=50 GROUP BY p2.id ORDER BY correct_answers DESC " +
                    ")," +
                    "places AS ( " +
                        "SELECT DISTINCT correct_answers FROM profile_answers " +
                    ")," +
                    "numbered_places AS(" +
                        "SELECT correct_answers, ROW_NUMBER () OVER (ORDER BY correct_answers DESC ) as place FROM places " +
                    ")" +
                    "SELECT np.place, np.correct_answers, p.* FROM profile_answers pa " +
                    "INNER JOIN numbered_places np ON pa.correct_answers = np.correct_answers " +
                    "INNER JOIN profile p on pa.id = p.id;",
                (resultSet, i) -> QuizSeriesParticipant.builder()
                        .username(resultSet.getString("username"))
                        .fullName(resultSet.getString("full_name"))
                        .id(resultSet.getLong("id"))
                        .correctAnswers(resultSet.getLong("correct_answers"))
                        .pictureUrl(resultSet.getString("picture_url"))
                        .place(resultSet.getLong("place"))
                        .genderId(resultSet.getLong("gender_id"))
                        .build(),
                hashtags.toArray());

    }
}

package lv.verku.viktorina.jdbc.dao;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@AllArgsConstructor
public class QuizSeriesLeaderboardDao {

    protected JdbcTemplate jdbcTemplate;

    public List<QuizSeriesParticipant> get(List<String> hashtags) {

        String inSql = String.join(",", Collections.nCopies(hashtags.size(), "?"));

        return jdbcTemplate.query(
                "WITH aggregated_quiz_hastags AS (  " +
                    "    SELECT qh.quiz_id, array_agg(hashtag) as hashtags  " +
                    "    FROM hashtag h  " +
                    "    INNER JOIN quiz_hashtag qh on h.id = qh.hashtag_id  " +
                    "    GROUP BY qh.quiz_id  " +
                    "),  " +
                    "series AS (  " +
                    "    SELECT *FROM aggregated_quiz_hastags WHERE ARRAY["+ inSql +"]::text[] <@ hashtags  " +
                    "),  " +
                    "participants AS (  " +
                    "    SELECT p.*, count(*) as correct_answers FROM series s  " +
                    "    INNER JOIN quiz q ON s.quiz_id = q.id  " +
                    "    INNER JOIN answer a ON a.quiz_tally_id = q.correct_tally_id  " +
                    "    INNER JOIN profile p on a.profile_id = p.id  " +
                    "    GROUP BY p.id  " +
                    "),  " +
                    "places AS (  " +
                    "    SELECT DISTINCT correct_answers FROM participants  " +
                    "),  " +
                    "numbered_places AS (  " +
                    "    SELECT correct_answers, ROW_NUMBER () OVER (ORDER BY correct_answers DESC ) as place FROM places  " +
                    ")  " +
                    "SELECT p.*, np.place FROM participants p  " +
                    "INNER JOIN  numbered_places np ON p.correct_answers = np.correct_answers ORDER BY np.place ASC;",
                (resultSet, i) -> QuizSeriesParticipant.builder()
                        .username(resultSet.getString("username"))
                        .fullName(resultSet.getString("full_name"))
                        .id(resultSet.getLong("id"))
                        .correctAnswers(resultSet.getLong("correct_answers"))
                        .pictureUrl(resultSet.getString("picture_url"))
                        .place(resultSet.getLong("place"))
                        .build(),
                hashtags.toArray()
        );

    }
}

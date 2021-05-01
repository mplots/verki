package lv.verku.viktorina.jdbc.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public abstract class BaseDao <T> {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public abstract void upsert(T entity);

    protected static Timestamp toDate(LocalDateTime time) {
        if (time == null) {
            return null;
        }

        return Timestamp.valueOf(time);
    }

    protected static LocalDateTime fromDate(Timestamp time) {
        if (time == null) {
            return null;
        }
        return time.toLocalDateTime();
    }
}

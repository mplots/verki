package lv.verku.viktorina.jdbc.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDao <T> {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public abstract void upsert(T entity);
}

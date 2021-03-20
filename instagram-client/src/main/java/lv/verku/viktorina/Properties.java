package lv.verku.viktorina;

import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Getter
public class Properties {
    @Value("#{environment.SERIES_TITLE ?: '3. sezona'}")
    private String seriesTitle;

    @Value("#{environment.RATE_LIMIT_SECONDS ?: '900'}")
    private Integer rateLimitSeconds;

    @Value("#{environment.HASHTAGS ?: 'verķuviktorīna'}")
    private List<String> hashtags;

    @Value("#{environment.JDBC_USERNAME ?: 'admin'}")
    private String jdbcUsername;

    @Value("#{environment.JDBC_PASSWORD ?: 'admin'}")
    private String jdbcPassword;

    @Value("#{environment.JDBC_HOST ?: 'localhost'}")
    private String jdbcHost;

    @Value("#{environment.JDBC_PORT ?: '5432'}")
    private String jdbcPort;

    @Value("#{environment.JDBC_MAIN_DATABASE ?: 'viktorina'}")
    private String jdbcMainDatabase;

    @Value("#{environment.JDBC_MAIN_DATABASE ?: 'quarz'}")
    private String jdbcQuartzDatabase;


    private RateLimiter rateLimiter;

    @PostConstruct
    public void initialize(){
        rateLimiter = RateLimiter.create(1.0/( rateLimitSeconds ));
    }
}

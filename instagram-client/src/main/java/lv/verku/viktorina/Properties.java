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

    @Value("#{environment.RATE_LIMIT_MINUTES ?: '1'}")
    private Integer rateLimitMinutes;

    @Value("#{environment.HASHTAGS ?: 'verķuviktorīna'}")
    private List<String> hashtags;

    private RateLimiter rateLimiter;

    @PostConstruct
    public void initialize(){
        rateLimiter = RateLimiter.create(1.0/( rateLimitMinutes * 60));
    }
}

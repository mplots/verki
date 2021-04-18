package lv.verku.viktorina.quartz;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.service.InstagramService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InstagramPullJob implements Job {

    private InstagramService instagramService;
    private Properties properties;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if(properties.getRateLimiter().tryAcquire()) {
            instagramService.pull();
        }
    }
}

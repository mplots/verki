package lv.verku.viktorina.quartz;

import org.quartz.*;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Component
public class InstagramScheduler {

    @Bean
    @QuartzDataSource
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(InstagramPullJob.class)
                .storeDurably()
                .withIdentity("Instagram_Pull_Job_Detail")
                .withDescription("Pulls details from instagram...")
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("Instagram_Trigger")
                .withDescription("Trigger instagram pull every hour")
                .withSchedule(simpleSchedule().repeatForever().withIntervalInHours(1))
                .build();
    }
}

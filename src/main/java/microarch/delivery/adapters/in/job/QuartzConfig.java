package microarch.delivery.adapters.in.job;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    private final int INTERVAL_SEC = 1;

    @Bean
    public JobDetail assignOrdersJobDetail() {
        return JobBuilder.newJob(AssignOrdersJob.class).withIdentity("assignOrdersJob").storeDurably().build();
    }

    @Bean
    public Trigger assignOrdersTrigger(JobDetail assignOrdersJobDetail) {
        return TriggerBuilder.newTrigger().forJob(assignOrdersJobDetail).withIdentity("assignOrdersTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(INTERVAL_SEC)
                        .repeatForever())
                .build();
    }
}

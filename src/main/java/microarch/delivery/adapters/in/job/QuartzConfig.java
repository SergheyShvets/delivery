package microarch.delivery.adapters.in.job;

import microarch.delivery.adapters.out.postgres.outbox.OutboxJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public Trigger assignOrdersTrigger(@Qualifier("assignOrdersJobDetail") JobDetail assignOrdersJobDetail) {
        return TriggerBuilder.newTrigger().forJob(assignOrdersJobDetail).withIdentity("assignOrdersTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(INTERVAL_SEC)
                        .repeatForever())
                .build();
    }

    @Bean
    public JobDetail outboxJobDetail() {
        return JobBuilder.newJob(OutboxJob.class).withIdentity("outboxJob").storeDurably().build();
    }

    @Bean
    public Trigger outboxTrigger(@Qualifier("outboxJobDetail") JobDetail outboxJobDetail) {
        return TriggerBuilder.newTrigger().forJob(outboxJobDetail).withIdentity("outboxTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();
    }
}

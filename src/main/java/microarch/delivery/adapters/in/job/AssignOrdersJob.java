package microarch.delivery.adapters.in.job;

import microarch.delivery.core.application.commands.AssignOrdersCommandHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssignOrdersJob implements Job {
    private final AssignOrdersCommandHandler assignOrdersCommandHandler;

    @Autowired
    public AssignOrdersJob(AssignOrdersCommandHandler assignOrdersCommandHandler) {
        this.assignOrdersCommandHandler = assignOrdersCommandHandler;
    }

    @Override
    public void execute(JobExecutionContext context) {
        assignOrdersCommandHandler.handle();
    }
}

package ot.batch.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ot.batch.jobs.BoardJobConfig;
import ot.batch.jobs.OpenApiJobConfig;

@Service
@RequiredArgsConstructor
public class ScheduledTasks {

    private final JobLauncher jobLauncher;
    private final OpenApiJobConfig openApiJobConfig;
    private final BoardJobConfig boardJobConfig;

    @Scheduled(cron = "0 0 0 * * *")
    public void runBoardClosedJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(boardJobConfig.boardJob(), params);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void runOpenApiJobConfig() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(openApiJobConfig.openApiJob(), params);
    }

}

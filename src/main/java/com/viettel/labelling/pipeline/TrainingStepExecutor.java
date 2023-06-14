package com.viettel.labelling.pipeline;

import com.viettel.labelling.entity.TrainingStep;
import org.jobrunr.jobs.context.JobContext;

public class TrainingStepExecutor extends PipelineStepExecutor {

    private TrainingStep trainingStep;

    public TrainingStepExecutor(TrainingStep trainingStep) {
        super();
        this.trainingStep = trainingStep;
    }

    @Override
    public void execute(JobContext jobContext) {
        //  Call training api
        jobContext.logger().info("Training step");
    }
}

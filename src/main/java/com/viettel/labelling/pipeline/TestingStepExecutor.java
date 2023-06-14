package com.viettel.labelling.pipeline;

import com.viettel.labelling.entity.TestingStep;
import org.jobrunr.jobs.context.JobContext;

public class TestingStepExecutor extends PipelineStepExecutor {

    private TestingStep testingStep;

    public TestingStepExecutor(TestingStep testingStep) {
        super();
        this.testingStep = testingStep;
    }

    @Override
    public void execute(JobContext jobContext) {
        jobContext.logger().info("Testing step");
    }
}

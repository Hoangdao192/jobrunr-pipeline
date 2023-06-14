package com.viettel.labelling.pipeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import org.jobrunr.jobs.context.JobContext;

public abstract class PipelineStepExecutor {

    public PipelineStepExecutor() {
    }

    public abstract void execute(JobContext jobContext);

}

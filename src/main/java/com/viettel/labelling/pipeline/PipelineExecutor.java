package com.viettel.labelling.pipeline;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.viettel.labelling.dto.JobStepDto;
import com.viettel.labelling.dto.TestingStepDto;
import com.viettel.labelling.dto.TrainingStepDto;
import com.viettel.labelling.entity.*;
import com.viettel.labelling.repository.*;
import lombok.Data;
import lombok.ToString;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Component
public class PipelineExecutor {

    @Autowired
    private PipelineRepository pipelineRepository;
    private Long pollIntervalInSecond = 5L;
    @Autowired
    private JobStepRepository jobStepRepository;
    @Autowired
    private TestingStepRepository testingStepRepository;
    @Autowired
    private TrainingStepRepository trainingStepRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private DatasetRepository datasetRepository;

    public void run(UUID jobId, JobContext jobContext) {
        Pipeline pipeline = pipelineRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Not find pipeline"));
        List<PipelineStepExecutor> stepExecutors = new ArrayList<>();
        List<JobStep> jobSteps = jobStepRepository.findAllByPipeline(pipeline);
        //  Create pipeline steps
        for (int i = 0; i < jobSteps.size(); ++i) {
            JobStep jobStep = jobSteps.get(i);
            if (jobStep.getType().equals(JobStepType.TRAINING)) {
                TrainingStep trainingStep = trainingStepRepository.findById(jobStep.getId())
                        .orElse(null);
                stepExecutors.add(
                        new TrainingStepExecutor(trainingStep)
                );
            } else if (jobStep.getType().equals(JobStepType.TESTING)) {
                TestingStep testingStep = testingStepRepository.findById(jobStep.getId())
                                .orElse(null);
                stepExecutors.add(
                        new TestingStepExecutor(testingStep)
                );
            }
        }

        for (int i = 0; i < stepExecutors.size(); ++i) {
            if (pipeline.getPaused()) {
                pause(pipeline);
            } else {
                stepExecutors.get(i).execute(jobContext);
            }
        }
    }

    private void pause(Pipeline pipeline) {
        while (pipeline.getPaused()) {
            Long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime <= pollIntervalInSecond) {
                //  Loop until pass interval time
            }
            //  Load job status again
            pipeline = pipelineRepository.findById(pipeline.getId()).orElse(null);
        }
    }


}

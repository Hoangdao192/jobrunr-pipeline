package com.viettel.labelling.mapper;

import com.viettel.labelling.dto.JobStepDto;
import com.viettel.labelling.dto.PipelineDto;
import com.viettel.labelling.dto.TestingStepDto;
import com.viettel.labelling.dto.TrainingStepDto;
import com.viettel.labelling.entity.JobStep;
import com.viettel.labelling.entity.TestingStep;
import com.viettel.labelling.entity.TrainingStep;
import org.aspectj.weaver.ast.Test;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public TrainingStepDto map(TrainingStep trainingStep) {
        TrainingStepDto trainingStepDto = new TrainingStepDto();
        trainingStepDto.setId(trainingStep.getId());
        trainingStepDto.setType(trainingStep.getJobStep().getType().name());
        trainingStepDto.setPipelineId(trainingStep.getJobStep().getPipeline().getId().toString());
        trainingStepDto.setModelId(trainingStep.getModel().getId());
        trainingStepDto.setDatasetId(trainingStep.getDataset().getId());
        trainingStepDto.setConfig(trainingStep.getConfig());
        return trainingStepDto;
    }

    public TestingStepDto map(TestingStep testingStep) {
        TestingStepDto testingStepDto = new TestingStepDto();
        testingStepDto.setId(testingStep.getId());
        testingStepDto.setType(testingStep.getJobStep().getType().name());
        testingStepDto.setPipelineId(testingStep.getJobStep().getPipeline().getId().toString());
        testingStepDto.setConfig(testingStep.getConfig());
        return testingStepDto;
    }

}

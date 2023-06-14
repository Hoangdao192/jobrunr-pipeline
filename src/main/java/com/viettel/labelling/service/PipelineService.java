package com.viettel.labelling.service;

import com.viettel.labelling.dto.JobStepDto;
import com.viettel.labelling.dto.PipelineDto;
import com.viettel.labelling.dto.TestingStepDto;
import com.viettel.labelling.dto.TrainingStepDto;
import com.viettel.labelling.entity.*;
import com.viettel.labelling.mapper.ModelMapper;
import com.viettel.labelling.pipeline.PipelineExecutor;
import com.viettel.labelling.pipeline.PipelineStepExecutor;
import com.viettel.labelling.repository.*;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;
import org.jobrunr.jobs.states.*;
import org.jobrunr.scheduling.JobBuilder;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.Page;
import org.jobrunr.storage.PageRequest;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PipelineService {

    @Autowired
    private PipelineRepository pipelineRepository;
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
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StorageProvider storageProvider;
    @Autowired
    private JobScheduler jobScheduler;
    @Autowired
    private PipelineExecutor pipelineExecutor;

    public PipelineDto createPipeline(PipelineDto pipelineDto) {
        JobId jobId = new JobId(UUID.randomUUID());
        Pipeline pipeline = new Pipeline();
        pipeline.setId(jobId.asUUID());
        pipeline = pipelineRepository.save(pipeline);

        List<JobStep> jobSteps = new ArrayList<>();
        List<JobStepDto> stepDtoList = pipelineDto.getSteps();
        //  Create pipeline steps
        for (int i = 0; i < stepDtoList.size(); ++i) {
            JobStepDto jobStepDto = stepDtoList.get(i);
            System.out.println(jobStepDto.getType());
            if (jobStepDto instanceof TrainingStepDto) {
                TrainingStepDto trainingStepDto = (TrainingStepDto) jobStepDto;
                Model model = modelRepository.findById(trainingStepDto.getModelId())
                        .orElse(null);
                Dataset dataset = datasetRepository.findById(trainingStepDto.getDatasetId())
                        .orElse(null);
                JobStep jobStep = jobStepRepository.save(
                        new JobStep(
                                JobStepType.TRAINING, pipeline
                        )
                );
                TrainingStep trainingStep = trainingStepRepository.save(
                        new TrainingStep(
                            jobStep, model, dataset, ""
                        )
                );
                jobSteps.add(jobStep);
            } else if (jobStepDto instanceof TestingStepDto) {
                TestingStepDto testingStepDto = (TestingStepDto) jobStepDto;
                JobStep jobStep = jobStepRepository.save(
                        new JobStep(
                                JobStepType.TESTING, pipeline
                        )
                );
                TestingStep testingStep = testingStepRepository.save(
                        new TestingStep(
                                jobStep, ""
                        )
                );
                jobSteps.add(jobStep);
            }
        }
        jobScheduler.create(JobBuilder.aJob()
                .scheduleAt(pipelineDto.getScheduledAt().toInstant(ZoneOffset.UTC))
                .withId(jobId.asUUID())
                .withName("Pipeline " + jobId.toString())
                .withDetails(() -> pipelineExecutor.run(UUID.fromString(jobId.toString()), JobContext.Null)));
        Job job = storageProvider.getJobById(jobId);
        pipeline.setSteps(jobSteps);
        return createDtoFromPipelineAndJobrunrJob(pipeline, job);
    }

    @Transactional
    public PipelineDto updatePipeLine(PipelineDto pipelineDto) {
        //  Checking if pipeline is able to update (SCHEDULED state only)
        Pipeline pipeline = pipelineRepository.findById(UUID.fromString(pipelineDto.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not exists."));
        Job job = storageProvider.getJobById(UUID.fromString(pipelineDto.getId()));
        if (!(job.getJobState() instanceof ScheduledState)) {
            throw new IllegalArgumentException("Pipeline cannot be update anymore.");
        }

        if (pipelineDto.getScheduledAt() != null) {
            job.scheduleAt(pipelineDto.getScheduledAt().toInstant(ZoneOffset.UTC),
                    "Update schedule time");
        }

        //  Remove old steps
        List<JobStep> jobSteps = jobStepRepository.findAllByPipeline(pipeline);
        jobSteps.forEach(jobStep -> {
            if (jobStep.getType().equals(JobStepType.TRAINING)) {
                trainingStepRepository.deleteById(jobStep.getId());
            } else if (jobStep.getType().equals(JobStepType.TESTING)) {
                testingStepRepository.deleteById(jobStep.getId());
            }
        });
        jobStepRepository.deleteAllByPipeline(pipeline);

        //  Create new step
        PipelineExecutor pipelineExecutor = new PipelineExecutor();
        List<PipelineStepExecutor> stepExecutors = new ArrayList<>();
        jobSteps = new ArrayList<>();
        List<JobStepDto> stepDtoList = pipelineDto.getSteps();
        //  Create pipeline steps
        for (int i = 0; i < stepDtoList.size(); ++i) {
            JobStepDto jobStepDto = stepDtoList.get(i);
            if (jobStepDto instanceof TrainingStepDto) {
                TrainingStepDto trainingStepDto = (TrainingStepDto) jobStepDto;
                Model model = modelRepository.findById(trainingStepDto.getModelId())
                        .orElse(null);
                Dataset dataset = datasetRepository.findById(trainingStepDto.getDatasetId())
                        .orElse(null);
                JobStep jobStep = jobStepRepository.save(
                        new JobStep(
                                JobStepType.TRAINING, pipeline
                        )
                );
                TrainingStep trainingStep = trainingStepRepository.save(
                        new TrainingStep(
                                jobStep, model, dataset, ""
                        )
                );
                jobSteps.add(jobStep);
            } else if (jobStepDto instanceof TestingStepDto) {
                TestingStepDto testingStepDto = (TestingStepDto) jobStepDto;
                JobStep jobStep = jobStepRepository.save(
                        new JobStep(
                                JobStepType.TESTING, pipeline
                        )
                );
                TestingStep testingStep = testingStepRepository.save(
                        new TestingStep(
                                jobStep, ""
                        )
                );
                jobSteps.add(jobStep);
            }
        }

//        JobDetails
        storageProvider.deletePermanently(UUID.fromString(pipelineDto.getId()));
        jobScheduler.create(JobBuilder.aJob()
                .scheduleAt(pipelineDto.getScheduledAt().toInstant(ZoneOffset.UTC))
                .withId(pipeline.getId())
                .withName("Pipeline " + pipeline.getId().toString())
                .withDetails(() -> pipelineExecutor.run(job.getId(), JobContext.Null)));
        Job newJob = storageProvider.getJobById(pipeline.getId());
        return createDtoFromPipelineAndJobrunrJob(pipeline, newJob);
    }

    @Transactional
    public String deletePipeLine(String id) {
        Pipeline pipeline = pipelineRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not exists."));
        //  Remove step
        List<JobStep> jobSteps = jobStepRepository.findAllByPipeline(pipeline);
        jobSteps.forEach(jobStep -> {
            if (jobStep.getType().equals(JobStepType.TRAINING)) {
                trainingStepRepository.deleteById(jobStep.getId());
            } else if (jobStep.getType().equals(JobStepType.TESTING)) {
                testingStepRepository.deleteById(jobStep.getId());
            }
        });
        jobStepRepository.deleteAllByPipeline(pipeline);

        pipelineRepository.delete(pipeline);
        storageProvider.deletePermanently(pipeline.getId());
        return pipeline.getId().toString();
    }

    public PipelineDto getPipeline(String id) {
        Pipeline pipeline = pipelineRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not exists."));
        Job job = storageProvider.getJobById(pipeline.getId());
        return createDtoFromPipelineAndJobrunrJob(pipeline, job);
    }

    public org.springframework.data.domain.Page<PipelineDto> getAllPipeline(
            String state, Integer page, Integer pageSize) {
        Page<Job> jobPage = storageProvider.getJobPage(
                StateName.valueOf(state), new PageRequest(
                        "updatedAt", page * pageSize, pageSize
                )
        );
        List<PipelineDto> pipelineDtos = new ArrayList<>();
        jobPage.getItems().forEach(job -> {
            Pipeline pipeline = pipelineRepository.findById(job.getId())
                    .orElseThrow(() -> new RuntimeException("Database state is not correct."));
            pipelineDtos.add(createDtoFromPipelineAndJobrunrJob(pipeline, job));
        });
        PageImpl<PipelineDto> pipelineDtoPage = new PageImpl<>(
                pipelineDtos, org.springframework.data.domain.PageRequest.of(
                        jobPage.getCurrentPage(), pageSize), jobPage.getTotal()
        );
        return pipelineDtoPage;
    }

    public String startPipeline(String id) {
        Pipeline pipeline = pipelineRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not exists."));
        //  Checking if pipeline not processing
        Job job = storageProvider.getJobById(pipeline.getId());
        if (job.getJobState() instanceof ProcessingState) {
            throw new IllegalArgumentException("Pipeline already started.");
        }

        job.enqueue();
        storageProvider.save(job);
        return "success";
    }

    public PipelineDto reschedule(String id, LocalDateTime localDateTime) {
        Pipeline pipeline = pipelineRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not exists."));
        //  Checking if pipeline not processing
        Job job = storageProvider.getJobById(pipeline.getId());
        if (!(job.getJobState() instanceof DeletedState || job.getJobState() instanceof ScheduledState)) {
            throw new IllegalArgumentException("Cannot reschedule job at this moment");
        }

        job.scheduleAt(localDateTime.toInstant(ZoneOffset.UTC),
                "Reschedule pipeline");
        storageProvider.save(job);
        return createDtoFromPipelineAndJobrunrJob(pipeline, job);
    }

    public String stopPipeline(String id) {
        Pipeline pipeline = pipelineRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not exists."));
        Job job = storageProvider.getJobById(pipeline.getId());
        job.delete("Stop job");
        storageProvider.save(job);
        return "success";
    }

    public String pausePipeline(String id) {
        Pipeline pipeline = pipelineRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not exists."));
        pipeline.setPaused(true);
        pipelineRepository.save(pipeline);
        return "success";
    }

    public String resumePipeline(String id) {
        Pipeline pipeline = pipelineRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not exists."));
        pipeline.setPaused(false);
        pipelineRepository.save(pipeline);
        return "success";
    }

    private PipelineDto createDtoFromPipelineAndJobrunrJob(Pipeline pipeline, Job job) {
        PipelineDto pipelineDto = new PipelineDto();
        pipelineDto.setId(pipeline.getId().toString());

        JobStep currentStep = pipeline.getCurrentStep();
        if (currentStep != null && currentStep.getType().equals(JobStepType.TRAINING)) {
            TrainingStep trainingStep = trainingStepRepository.findById(currentStep.getId())
                    .orElse(null);
            pipelineDto.setCurrentStep(modelMapper.map(trainingStep));
        } else if (currentStep != null && currentStep.getType().equals(JobStepType.TESTING)) {
            TestingStep testingStep = testingStepRepository.findById(currentStep.getId())
                    .orElse(null);
            pipelineDto.setCurrentStep(modelMapper.map(testingStep));
        }

        List<JobStepDto> stepDtoList = new ArrayList<>();
        pipeline.getSteps().forEach(jobStep -> {
            if (jobStep.getType().equals(JobStepType.TRAINING)) {
                TrainingStep trainingStep = trainingStepRepository.findById(jobStep.getId())
                        .orElse(null);
                stepDtoList.add(modelMapper.map(trainingStep));
            } else if (jobStep.getType().equals(JobStepType.TESTING)) {
                TestingStep testingStep = testingStepRepository.findById(jobStep.getId())
                        .orElse(null);
                stepDtoList.add(modelMapper.map(testingStep));
            }
        });
        pipelineDto.setSteps(stepDtoList);

        pipelineDto.setVersion(job.getVersion());
        pipelineDto.setJobSignature(job.getJobSignature());
        pipelineDto.setState(job.getState().name());
        pipelineDto.setCreatedAt(LocalDateTime.ofInstant(job.getCreatedAt(), ZoneOffset.UTC));
        pipelineDto.setUpdatedAt(LocalDateTime.ofInstant(job.getUpdatedAt(), ZoneOffset.UTC));

        pipelineDto.setScheduledAt(getJobScheduleTime(job));
        pipelineDto.setRecurringJobId(job.getRecurringJobId().orElse(null));
        //  TODO: find exactly log index
        pipelineDto.setLogs((JobDashboardLogger.JobDashboardLogLines)
                job.getMetadata().get("jobRunrDashboardLog-3"));
        pipelineDto.setHistory(job.getJobStates());
        return pipelineDto;
    }

    private LocalDateTime getJobScheduleTime(Job job) {
        JobState state = job.getJobStates()
                .stream().filter(jobState -> jobState instanceof ScheduledState)
                .findFirst().orElse(null);
        if (state != null) {
            return LocalDateTime.ofInstant(
                    ((ScheduledState) state).getScheduledAt(),
                    ZoneOffset.UTC);
        }
        return null;
    }


}

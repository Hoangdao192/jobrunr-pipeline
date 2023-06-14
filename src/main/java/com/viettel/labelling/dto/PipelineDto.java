package com.viettel.labelling.dto;

import com.viettel.labelling.entity.JobStep;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jobrunr.jobs.context.JobDashboardLogger;
import org.jobrunr.jobs.states.JobState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PipelineDto {

    private String id;
    private JobStepDto currentStep;
    private List<JobStepDto> steps;

    private Integer version;
    private String jobAsJson;
    private String jobSignature;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    private String recurringJobId;

    private JobDashboardLogger.JobDashboardLogLines logs;
    private List<JobState> history;

}

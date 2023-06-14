package com.viettel.labelling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingStepDto extends JobStepDto {


    private Long modelId;
    private Long datasetId;
    private String config;

}

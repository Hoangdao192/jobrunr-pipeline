package com.viettel.labelling.dto;

import com.viettel.labelling.entity.JobStep;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestingStepDto extends JobStepDto {


    private String config;

}

package com.viettel.labelling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "job_step")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private JobStepType type;

    @ManyToOne
    @JsonIgnore
    private Pipeline pipeline;

    public JobStep(JobStepType type, Pipeline pipeline) {
        this.type = type;
        this.pipeline = pipeline;
    }
}

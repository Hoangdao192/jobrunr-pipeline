package com.viettel.labelling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "testing_step")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestingStep {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private JobStep jobStep;

    private String config;

    public TestingStep(JobStep jobStep, String config) {
        this.jobStep = jobStep;
        this.config = config;
    }
}

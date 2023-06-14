package com.viettel.labelling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "training_step")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingStep {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private JobStep jobStep;

    @ManyToOne
    private Model model;

    @ManyToOne
    private Dataset dataset;

    private String config;

    public TrainingStep(JobStep jobStep, Model model, Dataset dataset, String config) {
        this.jobStep = jobStep;
        this.model = model;
        this.dataset = dataset;
        this.config = config;
    }
}

package com.viettel.labelling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Table(name = "pipeline")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pipeline {

    @Id
    @Type(type = "uuid-char")
    @Column(length = 36)
    private UUID id;

    @OneToOne
    @JsonIgnore
    private JobStep currentStep;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pipeline")
    private List<JobStep> steps;
    private Boolean paused = false;

}

package com.viettel.labelling.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TrainingStepDto.class, name = "TRAINING"),
        @JsonSubTypes.Type(value = TestingStepDto.class, name = "TESTING")
})
public abstract class JobStepDto {

    protected Long id;
    protected String type;
    protected String pipelineId;

}

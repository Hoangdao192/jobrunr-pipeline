package com.viettel.labelling.repository;

import com.viettel.labelling.entity.JobStep;
import com.viettel.labelling.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobStepRepository extends JpaRepository<JobStep, Long> {

    List<JobStep> findAllByPipeline(Pipeline pipeline);
    void deleteAllByPipeline(Pipeline pipeline);

}

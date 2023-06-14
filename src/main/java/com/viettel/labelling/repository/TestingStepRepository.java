package com.viettel.labelling.repository;

import com.viettel.labelling.entity.TestingStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestingStepRepository extends JpaRepository<TestingStep, Long> {
}

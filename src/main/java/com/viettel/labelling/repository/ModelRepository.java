package com.viettel.labelling.repository;

import com.viettel.labelling.entity.Model;
import com.viettel.labelling.entity.TrainingStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
}

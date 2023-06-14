package com.viettel.labelling.repository;

import com.viettel.labelling.entity.Dataset;
import com.viettel.labelling.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
}

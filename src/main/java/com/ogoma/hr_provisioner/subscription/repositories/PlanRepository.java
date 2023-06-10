package com.ogoma.hr_provisioner.subscription.repositories;

import com.ogoma.hr_provisioner.subscription.entities.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, Long> {

}

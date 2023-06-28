package com.ogoma.hr_provisioner.plans.repositories;

import com.ogoma.hr_provisioner.base.BaseRepository;
import com.ogoma.hr_provisioner.plans.entities.PlanEntity;
import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends BaseRepository<PlanEntity> {

}

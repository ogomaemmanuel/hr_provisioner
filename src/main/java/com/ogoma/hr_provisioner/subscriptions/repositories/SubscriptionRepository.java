package com.ogoma.hr_provisioner.subscriptions.repositories;

import com.ogoma.hr_provisioner.base.BaseRepository;
import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends BaseRepository<SubscriptionEntity> {
   public Optional<SubscriptionEntity> findTopByEmailAndArchiveOrderByCreatedAtDesc(String email, boolean archive);
   public Optional<SubscriptionEntity> findTopByEmailOrderByCreatedAtDesc(String email);
}

package com.ogoma.hr_provisioner.subscription.services;

import com.ogoma.hr_provisioner.subscription.repositories.PlanRepository;
import org.springframework.stereotype.Service;

@Service
public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }
}


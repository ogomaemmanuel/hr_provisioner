package com.ogoma.hr_provisioner.plans.controllers;

import com.ogoma.hr_provisioner.plans.PlanQuery;
import com.ogoma.hr_provisioner.plans.dtos.PlanDto;
import com.ogoma.hr_provisioner.plans.entities.PlanEntity;
import com.ogoma.hr_provisioner.plans.services.PlanService;
import com.ogoma.hr_provisioner.utils.reponses.Responder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plans")
// STATUS CODES TO BE HANDLED LATER
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<PlanEntity>> getAllPlans(PlanQuery query){
        return ResponseEntity.ok(this.planService.getAllPlans(query));
    }

    @PostMapping
    public ResponseEntity<HashMap<Object,Object>> addAPlan(@Valid @RequestBody PlanDto data){
        var record = this.planService.addAPlan(data);
        return this.formatStatusCode(record);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HashMap<Object,Object>> getSinglePlan(@PathVariable Long id){
        var record = this.planService.getSinglePlan(id);
        return this.formatStatusCode(record);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<Object,Object>> softDeleteAPlan(@PathVariable Long id){
        var successful = this.planService.softDeleteAPlan(id);
        return this.formatStatusCode(successful);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HashMap<Object,Object>> updatePlan(@PathVariable Long id,@RequestBody PlanDto data){
        var results = this.planService.updateAPlan(id,data);
        return this.formatStatusCode(results);
    }

    public ResponseEntity<HashMap<Object,Object>> formatStatusCode(HashMap<Object,Object> response){
        var status_code = response.get("status");
        response.remove("status");
        return ResponseEntity.status((Integer) status_code).body(response);
    }




}

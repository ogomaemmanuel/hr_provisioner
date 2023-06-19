package com.ogoma.hr_provisioner.plans.services;

import com.ogoma.hr_provisioner.plans.PlanQuery;
import com.ogoma.hr_provisioner.plans.dtos.PlanDto;
import com.ogoma.hr_provisioner.plans.entities.PlanEntity;
import com.ogoma.hr_provisioner.plans.repositories.PlanRepository;
import com.ogoma.hr_provisioner.utils.reponses.Responder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public HashMap<Object, Object> configureResponder(Map<Object, Object> returnables) {
        var responder = new Responder();
        returnables.forEach(responder::setReturnObj);
        return responder.getReturnObj();
    }

    public HashMap<Object, Object> getAllPlans() {
        var plans = this.planRepository.findAll();
        var respond = new HashMap<>();
        respond.put("data", plans);
        return respond;
    }

    private PlanEntity createAPlan(PlanDto data) {
        var plan = new PlanEntity();
        plan.setAmount(data.getAmount());
        plan.setName(data.getName());
        plan.setFeatures(data.getFeatures());
        plan.setType(data.getType());
        plan.setStatus(data.getStatus());
        plan.setArchive(false);
        return plan;
    }

    public HashMap<Object, Object> addAPlan(PlanDto data) {
        var plan = this.planRepository.save(createAPlan(data));
        var respond = new HashMap<>();
        respond.put("data", data);
        respond.put("status",201);
        return respond;
    }

    public HashMap<Object,Object> softDeleteAPlan(Long id) {
        var record = planRepository.findById(id);

        var responder = new HashMap<>();


        record.ifPresentOrElse(values->{
            if(!values.getArchive()) {
                values.setArchive(true);
                this.planRepository.save(values);
                responder.put("message", "Deleted successfully");
                responder.put("status", 200);
            }else{
                responder.put("error","Record with the given id doesn't exists");
                responder.put("status",400);
            }
        },()->{
            responder.put("error","Record with given id doesn't exist");
            responder.put("status",400);
        });
        return responder;
    }

    public HashMap<Object, Object> getSinglePlan(Long id) {
        var plan = planRepository.findById(id);
        var respond = new HashMap<>();
        plan.ifPresentOrElse(results->{
            if(results.getArchive()){
                respond.put("error","Record with the given id doesn't exist");
                respond.put("status",400);
            }else{
                respond.put("data", plan);
                respond.put("status",200);
            }
        },()->{
            respond.put("error","Record with the given id doesn't exist");
            respond.put("status",400);
        });
        return respond;
    }


    public List<PlanEntity> getAllPlans(PlanQuery query) {
        // RESET ARCHIVE TO FALSE
        query.setArchive(false);
        return this.planRepository.findAll(query.toSpec());
    }

    public HashMap<Object, Object> updateAPlan(Long id, PlanDto data) {
        var results = this.planRepository.findById(id);
        var responder = new HashMap<>();
        results.ifPresentOrElse(result->{

            if(!result.getArchive()) {

                if (data.getAmount() != null) {
                    result.setAmount(data.getAmount());
                }

                if (data.getFeatures() != null) {
                    result.setFeatures(data.getFeatures());
                }

                if (data.getName() != null) {
                    result.setName(data.getName());
                }

                if (data.getStatus() != null) {
                    result.setStatus(data.getStatus());
                }

                if (data.getType() != null) {
                    result.setType(data.getType());
                }

                this.planRepository.save(result);
                responder.put("data","Updated successfully");
                responder.put("status",200);
            }else{
                responder.put("error","Record with the given id doesn't exists");
                responder.put("status",400);
            }

        },()->{
            responder.put("errors","Record with the given id doesn't exists");
            responder.put("status",400);
        });
        return responder;
    }
}


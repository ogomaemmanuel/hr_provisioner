package com.ogoma.hr_provisioner.plans.dtos;

import com.ogoma.hr_provisioner.plans.enums.Status;
import com.ogoma.hr_provisioner.plans.enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.List;

public class PlanDto {
    @NotNull(message = "Plan Type is required.")
    @Enumerated(EnumType.STRING)
    private Type type;
    @NotNull(message = "Plan Amount is required.")
    private Integer amount;
    @NotBlank(message = "Plan Name is required.")
    private String name;
    private List<String> features;
    @NotNull(message = "Plan Status is required.")
    @Enumerated(EnumType.STRING)
    private Status status;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

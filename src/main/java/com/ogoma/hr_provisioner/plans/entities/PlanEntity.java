package com.ogoma.hr_provisioner.plans.entities;


import com.ogoma.hr_provisioner.base.BaseEntity;
import com.ogoma.hr_provisioner.plans.enums.Status;
import com.ogoma.hr_provisioner.plans.enums.Type;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;



@Entity
public class PlanEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private Type type;
    private Integer amount;

    private String name;

    @ElementCollection
    private List<String> features;

    private Boolean archive;

    @Enumerated(EnumType.STRING)
    private Status status;


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
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


    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}

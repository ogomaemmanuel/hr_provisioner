package com.ogoma.hr_provisioner.plans;

import com.ogoma.hr_provisioner.plans.entities.PlanEntity;
import com.ogoma.hr_provisioner.plans.enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class PlanQuery {
    private boolean archive;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Specification<PlanEntity> toSpec() {
        return new Specification<PlanEntity>() {
            @Override
            public Predicate toPredicate(Root<PlanEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                Predicate p = criteriaBuilder.conjunction();
                if (name != null) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("name"), name + "%"));
                }
                if (type != null) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type"), type));
                }
                p = criteriaBuilder.and(p,criteriaBuilder.equal(root.get("archive"),archive));
                return p;
            }
        };
    }
}

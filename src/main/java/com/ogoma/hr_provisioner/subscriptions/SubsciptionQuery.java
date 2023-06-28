package com.ogoma.hr_provisioner.subscriptions;

import com.ogoma.hr_provisioner.plans.entities.PlanEntity;
import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SubsciptionQuery {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;
    private String subDomain;
    private Long planId;
    private boolean archive;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }


    public Specification<SubscriptionEntity> toSpec() {
        return new Specification<SubscriptionEntity>() {
            @Override
            public Predicate toPredicate(Root<SubscriptionEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                Predicate p = criteriaBuilder.conjunction();
                if (firstName != null) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("firstName"), firstName + "%"));
                }
                if (lastName != null) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("lastName"), lastName + "%"));
                }

                if (email != null) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("email"), email));
                }
                if (subDomain != null) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("subDomain"), subDomain));
                }
                if (phoneNumber != null) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber));
                }
                p = criteriaBuilder.and(p,criteriaBuilder.equal(root.get("archive"),archive));
                return p;
            }
        };
    }
}

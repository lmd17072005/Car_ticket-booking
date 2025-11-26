package com.ra.base_spring_boot.specification;

import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.constants.BusStatus;
import com.ra.base_spring_boot.model.constants.BusType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BusSpecification {

    public static Specification<Bus> filterBuses(String search, BusStatus status , BusType type) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("licensePlate")), pattern),
                        cb.like(cb.lower(root.get("name")), pattern)
                ));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("busType"), type));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class LoanSpecification implements Specification<Loan> {
    private LoanSearchDto criteria;

    public LoanSpecification(LoanSearchDto criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Loan> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.conjunction();

        // Filtro Juego
        if (criteria.getGameId() != null) {
            predicate = builder.and(predicate, builder.equal(root.get("game").get("id"), criteria.getGameId()));
        }

        // Filtro Cliente
        if (criteria.getClientId() != null) {
            predicate = builder.and(predicate, builder.equal(root.get("client").get("id"), criteria.getClientId()));
        }

        // Filtro Fecha exacta (Comprueba fecha esta DENTRO del rango del préstamo)
        if (criteria.getDate() != null) {
            Predicate loanDateIsBeforeOrEqual = builder.lessThanOrEqualTo(root.get("loanDate"), criteria.getDate());
            Predicate returnDateIsAfterOrEqual = builder.greaterThanOrEqualTo(root.get("returnDate"), criteria.getDate());

            predicate = builder.and(predicate, loanDateIsBeforeOrEqual, returnDateIsAfterOrEqual);
        }

        return predicate;
    }
}

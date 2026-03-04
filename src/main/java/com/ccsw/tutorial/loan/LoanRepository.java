package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface LoanRepository extends CrudRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Loan l " + "WHERE l.game.id = :gameId " + "AND l.loanDate <= :loanDate " + "AND l.returnDate >= :loanDate " + "AND (:loanId IS NULL OR l.id <> :loanId)")
    boolean existsByGameIdAndDates(@Param("gameId") long gameId, @Param("loanDate") LocalDate loanDate, @Param("returnDate") LocalDate returnDate, @Param("loanId") Long loanId);

    @Query("SELECT COUNT(l) FROM Loan l " + "WHERE l.client.id = :clientId " + "AND l.loanDate <= :returnDate " + "AND l.returnDate >= :loanDate " + "AND (:loanId IS NULL OR l.id <> :loanId)")
    Long countByClientIdAndDates(@Param("clientId") Long clientId, @Param("loanDate") LocalDate loanDate, @Param("returnDate") LocalDate returnDate, @Param("loanId") Long loanId);
}

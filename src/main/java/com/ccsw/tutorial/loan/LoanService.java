package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.springframework.data.domain.Page;

public interface LoanService {
    Page<Loan> findPage(LoanSearchDto dto);

    void save(Long id, LoanDto dto);

    void delete(Long id);
}

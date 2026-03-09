package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanTest {
    @Mock
    private LoanRepository loanRepository;

    @Mock
    private com.ccsw.tutorial.client.ClientService clientService;

    @Mock
    private com.ccsw.tutorial.game.GameService gameService;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    public void saveWithReturnDateBeforeLoanDateShouldThrowException() {
        LoanDto dto = new LoanDto();
        dto.setLoanDate(LocalDate.of(2026, 3, 10));
        dto.setReturnDate(LocalDate.of(2026, 3, 5));

        assertThrows(ResponseStatusException.class, () -> loanService.save(null, dto));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void saveWithPeriodGreaterThan14DaysShouldThrowException() {
        LoanDto dto = new LoanDto();
        dto.setLoanDate(LocalDate.of(2026, 3, 1));
        dto.setReturnDate(LocalDate.of(2026, 3, 20));

        assertThrows(ResponseStatusException.class, () -> loanService.save(null, dto));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void saveWithGameAlreadyLentInPeriodShouldThrowException() {
        LoanDto dto = createValidLoanDto();

        when(loanRepository.existsByGameIdAndDates(anyLong(), any(), any(), any())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> loanService.save(null, dto));

        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void saveWithClientHavingMoreThanTwoGamesLentInPeriodShouldThrowException() {
        LoanDto dto = createValidLoanDto();

        when(loanRepository.existsByGameIdAndDates(anyLong(), any(), any(), any())).thenReturn(false);
        when(loanRepository.countByClientIdAndDates(anyLong(), any(), any(), any())).thenReturn(2L);

        assertThrows(ResponseStatusException.class, () -> loanService.save(null, dto));

        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void saveWithValidDataShouldInsert() {
        LoanDto dto = createValidLoanDto();

        when(loanRepository.existsByGameIdAndDates(anyLong(), any(), any(), any())).thenReturn(false);
        when(loanRepository.countByClientIdAndDates(anyLong(), any(), any(), any())).thenReturn(0L);

        when(clientService.get(anyLong())).thenReturn(new Client());
        when(gameService.get(anyLong())).thenReturn(new Game());

        loanService.save(null, dto);

        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanCaptor.capture());

        assertEquals(dto.getLoanDate(), loanCaptor.getValue().getLoanDate());
    }

    private LoanDto createValidLoanDto() {
        LoanDto dto = new LoanDto();
        dto.setLoanDate(LocalDate.of(2026, 3, 1));
        dto.setReturnDate(LocalDate.of(2026, 3, 10));

        GameDto game = new GameDto();
        game.setId(1L);
        dto.setGame(game);

        ClientDto client = new ClientDto();
        client.setId(1L);
        dto.setClient(client);

        return dto;
    }

    @Test
    public void findPageShouldReturnPageOfLoans() {
        LoanSearchDto dto = new LoanSearchDto();
        PageableRequest pageable = new PageableRequest();
        pageable.setPageNumber(0);
        pageable.setPageSize(5);
        dto.setPageable(pageable);

        List<Loan> list = new java.util.ArrayList<>();
        list.add(mock(Loan.class));
        Page<Loan> expectedPage = new PageImpl<>(list);

        when(loanRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<Loan> page = loanService.findPage(dto);

        assertNotNull(page);
        assertEquals(1, page.getContent().size());
    }

    public static final Long EXISTS_LOAN_ID = 1L;
    public static final Long NOT_EXISTS_LOAN_ID = 0L;

    @Test
    public void deleteExistsLoanIdShouldDelete() {
        Loan loan = mock(Loan.class);
        when(loanRepository.findById(EXISTS_LOAN_ID)).thenReturn(java.util.Optional.of(loan));

        loanService.delete(EXISTS_LOAN_ID);

        verify(loanRepository).deleteById(EXISTS_LOAN_ID);
    }

    @Test
    public void deleteNotExistsLoanIdShouldThrowException() {
        when(loanRepository.findById(NOT_EXISTS_LOAN_ID)).thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class, () -> loanService.delete(NOT_EXISTS_LOAN_ID));

        verify(loanRepository, never()).deleteById(anyLong());
    }
}

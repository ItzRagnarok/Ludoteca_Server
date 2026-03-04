package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        when(clientService.get(anyLong())).thenReturn(new com.ccsw.tutorial.client.model.Client());
        when(gameService.get(anyLong())).thenReturn(new com.ccsw.tutorial.game.model.Game());

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
}

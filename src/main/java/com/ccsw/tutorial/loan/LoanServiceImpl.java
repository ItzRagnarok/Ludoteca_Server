package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;

@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private GameService gameService;

    @Override
    public Page<Loan> findPage(LoanSearchDto dto) {
        LoanSpecification spec = new LoanSpecification(dto);
        Pageable pageable = PageRequest.of(
                dto.getPageable().getPageNumber(),
                dto.getPageable().getPageSize()
        );

        return loanRepository.findAll(spec, pageable);
    }

    @Override
    public void save(Long id, LoanDto dto) {
        // La fecha de fin NO podrá ser anterior a la fecha de inicio
        if (dto.getReturnDate().isBefore(dto.getLoanDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin no puede ser anterior a la de inicio.");
        }

        // El periodo de préstamo máximo solo podrá ser de 14 días
        long daysBetween = ChronoUnit.DAYS.between(dto.getLoanDate(), dto.getReturnDate());
        if (daysBetween > 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El periodo de préstamo no puede exceder los 14 días.");
        }

        // El mismo juego no puede estar prestado en esas fechas
        boolean gameAlreadyLent = loanRepository.existsByGameIdAndDates(
                dto.getGame().getId(), dto.getLoanDate(), dto.getReturnDate(), id);
        if (gameAlreadyLent) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El juego ya está prestado en estas fechas.");
        }

        // Un cliente no puede tener más de 2 juegos en un mismo día
        Long clientConcurrentLoans = loanRepository.countByClientIdAndDates(
                dto.getClient().getId(), dto.getLoanDate(), dto.getReturnDate(), id);
        if (clientConcurrentLoans >= 2) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cliente ya tiene 2 juegos prestados en estas fechas.");
        }

        // Si pasa todas las validaciones
        Loan loan;
        if (id == null) {
            loan = new Loan();
        } else {
            loan = loanRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));
        }

        // Mapeo los datos del DTO a la Entidad
        loan.setLoanDate(dto.getLoanDate());
        loan.setReturnDate(dto.getReturnDate());
        loan.setGame(gameService.get(dto.getGame().getId()));
        loan.setClient(clientService.get(dto.getClient().getId()));

        // Guarda en base de datos
        loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) {
        if (loanRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado");
        }
        loanRepository.deleteById(id);
    }
}

package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {
    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<Map<String, Object>> responseTypePage = new ParameterizedTypeReference<Map<String, Object>>() {};

//    @Test
//    public void findPageShouldReturnPageOfLoans() {
//        LoanSearchDto searchDto = new LoanSearchDto();
//        PageableRequest pageable = new PageableRequest();
//        pageable.setPageNumber(0);
//        pageable.setPageSize(5);
//        searchDto.setPageable(pageable);
//
//        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
//                LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody().get("content"));
//    }

    @Test
    public void saveWithValidDataShouldCreateNewLoan() {
        LoanDto dto = new LoanDto();
        dto.setLoanDate(LocalDate.of(2026, 5, 1));
        dto.setReturnDate(LocalDate.of(2026, 5, 10));

        GameDto game = new GameDto();
        game.setId(1L);
        dto.setGame(game);

        ClientDto client = new ClientDto();
        client.setId(1L);
        dto.setClient(client);

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void saveWithReturnDateBeforeLoanDateShouldReturnBadRequest() {
        LoanDto dto = new LoanDto();
        // Ponemos la fecha de fin ANTES que la de inicio
        dto.setLoanDate(LocalDate.of(2026, 5, 10));
        dto.setReturnDate(LocalDate.of(2026, 5, 1));

        GameDto game = new GameDto();
        game.setId(1L);
        dto.setGame(game);

        ClientDto client = new ClientDto();
        client.setId(1L);
        dto.setClient(client);

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        // Comprobamos que el Controller escupe un 400 (Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    public static final Long DELETE_LOAN_ID = 1L;

    @Test
    public void deleteWithExistsIdShouldDeleteLoan() {
        // Asumimos que el data.sql tiene al menos un préstamo con ID 1
        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/" + DELETE_LOAN_ID, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}

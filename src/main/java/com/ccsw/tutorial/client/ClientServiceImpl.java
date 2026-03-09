package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    @Autowired
    ClientRepository clientRepository;

    @Override
    public Client get(Long id) {

        return this.clientRepository.findById(id).orElse(null);
    }

    @Override
    public List<Client> findAll() {
        return (List<Client>) this.clientRepository.findAll();
    }

    @Override
    public void save(Long id, ClientDto dto) {
        // Validación para evitar clientes duplicados
        if (this.clientRepository.existsByName(dto.getName())) {
            //throw new RuntimeException("El cliente con nombre " + dto.getName() + " ya existe");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre ya existe");

        }

        Client client;

        if (id == null) {
            client = new Client();
        } else {
            client = this.clientRepository.findById(id).orElse(null);
        }

        client.setName(dto.getName());

        this.clientRepository.save(client);
    }

    @Override
    public void delete(Long id) throws Exception {
        if (this.clientRepository.findById(id).orElse(null) == null) {
            throw new Exception("Not Exists");
        }

        this.clientRepository.deleteById(id);
    }
}

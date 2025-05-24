package com.bankapp.controller;

import com.bankapp.model.Client;
import com.bankapp.repository.ClientRepository;
import com.bankapp.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @PostMapping("/saveClient")
    public ResponseEntity<String> saveClient(@RequestBody Client client) {
        ClientRepository.save(client);
        return ResponseEntity.ok("В приложение добавлен клиент " + client.getUsername());
    }
}
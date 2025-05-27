package com.bankapp.controller;

import com.bankapp.model.Client;
import com.bankapp.repository.ClientRepository;
import com.bankapp.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {
    @Operation(
            summary = "Сохранение клиента, созданного в другом сервисе",
            description = "Служебный эндпоинт, регистрирует принятого пользователя в базе данного приложения",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные клиента для регистрации",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Client.class),
                            examples = @ExampleObject(
                                    name = "Пример клиента",
                                    summary = "Стандартный клиент",
                                    value = """
{
"id": "30a5a46d-76d1-43c4-8b2a-011249449a11",
"fullName": "Jojo Bean",
"phone": "+79533289595",
"username": "user88",
"password": "pass88",
"accounts": [
	{
	"id": "ecb9798a-38ba-4782-97ff-f1b44bb38437",
	"accountNumber": "4470cb08445e",
	"cardNumber": "dac97b7c7f3743ce",
	"balance": 8830
	}
]
}
                """
                            )
                    )
            ))
    @PostMapping("/saveClient")
    public ResponseEntity<String> saveClient(@RequestBody Client client) {
        ClientRepository.save(client);
        return ResponseEntity.ok("В приложение добавлен клиент " + client.getUsername());
    }
}
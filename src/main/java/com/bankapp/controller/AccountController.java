package com.bankapp.controller;

import com.bankapp.model.Account;
import com.bankapp.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    public AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @Operation(
            summary = "Создание нового счета для клиента",
            description = "Добавляет указанному клиенту новый счет со случайным номером",
            parameters = {
                    @Parameter(
                            name = "clientId",
                            description = "ID клиента (UUID)",
                            required = true,
                            example = "30a5a46d-76d1-43c4-8b2a-011249449a11",
                            schema = @Schema(type = "string", format = "uuid", minLength = 36, maxLength = 36)
                    )
            })
    @PostMapping("/create")
    public Account create(@RequestParam String clientId) {
        return accountService.createAccount(clientId);
    }
}

package com.bankapp.controller;

import com.bankapp.model.Account;
import com.bankapp.model.Client;
import com.bankapp.repository.ClientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private Client recipientClient;
    private Account recipientAccount;
    private final RestTemplate template = new RestTemplate();


    // 1️⃣ Получить список всех клиентов перед переводом
    @Operation(
            summary = "Получение списка всех клиентов перед переводом",
            description = "Выводит список всех зарегистрированных клиентов")
    @GetMapping("/clients")
    public List<Client> getAllClients() {
        return List.copyOf(ClientRepository.getAllClients());
    }

    // 2️⃣ Выбрать получателя перевода по телефону и номеру счета
    @Operation(
            summary = "Выбор получателя перевода по логину пользователя и номеру счета",
            description = "Выбирает получателя перевода. Отправитель перевода должен предварительно выполнить вход в систему.",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "Логин получателя перевода",
                            required = true,
                            example = "user1",
                            schema = @Schema(type = "string", minLength = 4, maxLength = 20)
                    ),
                    @Parameter(
                            name = "accountNumber",
                            description = "Номер счета получателя перевода",
                            required = true,
                            example = "4470cb08445e",
                            schema = @Schema(type = "string", minLength = 12, maxLength = 12)
                    )
            })
    @PostMapping("/select-recipient")
    public String selectRecipient(@RequestParam String username, @RequestParam String accountNumber) {
        boolean isLogged = Boolean.TRUE.equals(template.getForEntity("http://localhost:8081/auth/isLogged", boolean.class).getBody());
        if (!isLogged) {
            return "❌ Ошибка: Сначала войдите в систему!";
        }
        Optional<Client> recipientOpt = ClientRepository.findByUsername(username);
        if (recipientOpt.isEmpty()) {
            return "❌ Ошибка: Получатель не найден!";
        }

        Optional<Account> recipientAccountOpt = recipientOpt.get().getAccounts()
                .stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst();

        if (recipientAccountOpt.isEmpty()) {
            return "❌ Ошибка: У получателя нет такого счета!";
        }

        this.recipientClient = recipientOpt.get();
        this.recipientAccount = recipientAccountOpt.get();

        return "✅ Получатель выбран: " + recipientClient.getFullName() + " (Счет: " + recipientAccount.getAccountNumber() + ")";
    }

    // 3️⃣ Выполнить перевод (указать сумму и изменить баланс)
    @Operation(
            summary = "Выполнение перевода",
            description = "Переводит указанную сумму со счета отправителя на счет получателя. Предварительно должен быть выбран получатель перевода.",
            parameters = {
                    @Parameter(
                            name = "amount",
                            description = "Сумма для перевода",
                            required = true,
                            example = "500",
                            schema = @Schema(type = "number", format = "double", minimum = "0")
                    )
            })
    @PostMapping("/transfer")
    public String transfer(@RequestParam double amount) {
        boolean isLogged = Boolean.TRUE.equals(template.getForEntity("http://localhost:8081/auth/isLogged", boolean.class).getBody());

        if (!isLogged) {
            return "❌ Ошибка: Сначала войдите в систему!";
        }
        if (recipientClient == null || recipientAccount == null) {
            return "❌ Ошибка: Сначала выберите получателя!";
        }

        Client sender = template.getForEntity("http://localhost:8081/auth/getLoggedInClient", Client.class).getBody();
        if (sender == null) {
            return "❌ Ошибка: Попробуйте попозже!";
        }

        Optional<Account> senderAccountOpt = sender.getAccounts().stream().findFirst();
        if (senderAccountOpt.isEmpty()) {
            return "❌ Ошибка: У вас нет счета!";
        }

        Account senderAccount = senderAccountOpt.get();

        if (senderAccount.getBalance() < amount) {
            return "❌ Ошибка: Недостаточно средств на счете!";
        }

        // Обновляем балансы
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        recipientAccount.setBalance(recipientAccount.getBalance() + amount);

        return "✅ Перевод завершен! " + amount + "₽ переведено на счет " + recipientAccount.getAccountNumber();
    }
}
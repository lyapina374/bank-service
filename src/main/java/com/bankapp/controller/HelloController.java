package com.bankapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Operation(
            summary = "Приветствие",
            description = "Выводит \"Привет, (Имя)!\"",
            parameters = {
                    @Parameter(
                            name = "name",
                            description = "Имя для приветствия",
                            required = true,
                            example = "Гость",
                            schema = @Schema(type = "string")
                    )
            })
    @GetMapping("/hello")
    public String sayHello(@RequestParam(defaultValue = "Гость") String name) {
        return "Привет, " + name + "!";
    }
}
package com.nttdata.apiresttransactions.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AccountDto {
    private String id;
    private AccountTypeDto accountType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creationDate = LocalDate.now();
    private String accountNumber;
    private String currency;
    private double amount; //monto
    private double balance;//saldo
    private CustomerDto customer;
    private String state;
    private int maxLimitMovementPerMonth;
    private boolean principal;
    private List<HeadLineDto> headlines;
    private List<AuthorizedSignerDto> authorizedSigners;
}

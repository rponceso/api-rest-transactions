package com.nttdata.apiresttransactions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nttdata.apiresttransactions.dto.AccountDto;
import com.nttdata.apiresttransactions.dto.CreditCardDto;
import com.nttdata.apiresttransactions.dto.CreditDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String paymentType;
    private AccountDto account;
    private CreditDto credit;
    private CreditCardDto creditCard;
    private double amount;
    private String details;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOperation = LocalDateTime.now();
}

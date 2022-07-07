package com.nttdata.apiresttransactions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nttdata.apiresttransactions.dto.AccountDto;
import com.nttdata.apiresttransactions.dto.CustomerDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "deposits")
public class Deposit {

    @Id
    private String id;
    private AccountDto accountRoot;
    private AccountDto accountDestination;
    private double amount;
    private String details;
    private CustomerDto depositor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOperation = LocalDateTime.now();
}

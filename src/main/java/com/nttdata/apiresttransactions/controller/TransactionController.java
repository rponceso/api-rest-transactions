/**
 * Controller that receives the requests
 *
 * @author Renato Ponce
 * @version 1.0
 * @since 2022-06-24
 */

package com.nttdata.apiresttransactions.controller;

import com.nttdata.apiresttransactions.model.Deposit;
import com.nttdata.apiresttransactions.model.Payment;
import com.nttdata.apiresttransactions.model.Transaction;
import com.nttdata.apiresttransactions.model.Withdrawal;
import com.nttdata.apiresttransactions.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService service;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Transaction>> getById(@PathVariable("id") String id) {
        return service.getById(id)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                ); //Mono<ResponseEntity<Transaction>>
    }

    @GetMapping("/account/{id}")
    public Mono<ResponseEntity<Transaction>> getByAccountId(@PathVariable("id") String idAccount) {
        return service.getByAccountId(idAccount)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                ); //Mono<ResponseEntity<Transaction>>
    }


    @PostMapping
    public Mono<ResponseEntity<Transaction>> register(@RequestBody Transaction transaction, final ServerHttpRequest req) {
        //201 | localhost:8080/transactions/123
        return service.register(transaction)
                .map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

    @PostMapping("/deposit")
    public Mono<ResponseEntity<Deposit>> registerDeposit(@RequestBody Deposit deposit, final ServerHttpRequest req) {
        //201 | localhost:8080/transactions/123
        return service.registerDeposit(deposit)
                .map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

    @PostMapping("/withdrawal")
    public Mono<ResponseEntity<Withdrawal>> registerWithdrawal(@RequestBody Withdrawal withdrawal, final ServerHttpRequest req) {
        //201 | localhost:8080/transactions/123
        return service.registerWithdrawal(withdrawal)
                .map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

    @PostMapping("/withdrawal/debitcard")
    public Mono<ResponseEntity<Withdrawal>> registerWithdrawalWithDebitCard(@RequestBody Withdrawal withdrawal, final ServerHttpRequest req) {
        //201 | localhost:8080/transactions/123
        return service.registerWithdrawalWithDebitCard(withdrawal)
                .map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

    @PostMapping("/payment")
    public Mono<ResponseEntity<Payment>> registerPayment(@RequestBody Payment payment, final ServerHttpRequest req) {
        //201 | localhost:8080/transactions/123
        return service.registerPayment(payment)
                .map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

    @PostMapping("/payment/debitcard")
    public Mono<ResponseEntity<Payment>> registerPaymentWithDebitCard(@RequestBody Payment payment, final ServerHttpRequest req) {
        //201 | localhost:8080/transactions/123
        return service.registerPaymentWithDebitCard(payment)
                .map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

    @GetMapping("/movements")
    public Mono<ResponseEntity<Map<String, Object>>> getMovements() {
        return service.getMovements()
                .map(r -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(r)
                );
    }

}

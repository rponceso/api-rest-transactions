/**
 * Interface Service Transaction
 *
 * @author Renato Ponce
 * @version 1.0
 * @since 2022-06-24
 */

package com.nttdata.apiresttransactions.service;

import com.nttdata.apiresttransactions.model.Deposit;
import com.nttdata.apiresttransactions.model.Payment;
import com.nttdata.apiresttransactions.model.Transaction;
import com.nttdata.apiresttransactions.model.Withdrawal;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TransactionService {

    Mono<Transaction> getById(String id);

    Mono<Transaction> register(Transaction transaction);

    Mono<Transaction> getByAccountId(String idAccount);

    Mono<Deposit> registerDeposit(Deposit deposit);

    Mono<Withdrawal> registerWithdrawal(Withdrawal withdrawal);

    Mono<Withdrawal> registerWithdrawalWithDebitCard(Withdrawal withdrawal);

    Mono<Payment> registerPayment(Payment payment);

    Mono<Payment> registerPaymentWithDebitCard(Payment payment);

    Mono<Map<String, Object>> getMovements();
}

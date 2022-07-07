/**
 * Implementation Interface Service Transaction
 *
 * @author Renato Ponce
 * @version 1.0
 * @since 2022-06-24
 */

package com.nttdata.apiresttransactions.service;

import com.nttdata.apiresttransactions.dto.AccountDto;
import com.nttdata.apiresttransactions.dto.CreditCardDto;
import com.nttdata.apiresttransactions.dto.CreditDto;
import com.nttdata.apiresttransactions.dto.DebitCardDto;
import com.nttdata.apiresttransactions.model.Deposit;
import com.nttdata.apiresttransactions.model.Payment;
import com.nttdata.apiresttransactions.model.Transaction;
import com.nttdata.apiresttransactions.model.Withdrawal;
import com.nttdata.apiresttransactions.repository.DepositRepository;
import com.nttdata.apiresttransactions.repository.PaymentRepository;
import com.nttdata.apiresttransactions.repository.TransactionRepository;
import com.nttdata.apiresttransactions.repository.WithdrawalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private APIClients apiClients;


    @Override
    public Mono<Transaction> getById(String id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Transaction> register(Transaction transaction) {
        return validateSaveTransaction(transaction);
    }

    @Override
    public Mono<Transaction> getByAccountId(String idAccount) {
        return transactionRepository.findByAccount_Id(idAccount);
    }

    @Override
    public Mono<Deposit> registerDeposit(Deposit deposit) {
        Mono<AccountDto> monoAccountRoot = apiClients.findByAccount(deposit.getAccountRoot().getId());
        Mono<AccountDto> monoAccountDestination = apiClients.findByAccount(deposit.getAccountDestination().getId());

        return monoAccountRoot
                .flatMap(ar -> monoAccountDestination.flatMap(ad -> {
                    if (ar.getBalance() >= deposit.getAmount()) {
                        ar.setBalance(ar.getBalance() - deposit.getAmount());
                        ad.setBalance(ad.getBalance() + deposit.getAmount());
                    } else {
                        log.info("You don't have enough balance");
                    }
                    return apiClients.updateAccount(ad);
                }).flatMap(a -> depositRepository.save(deposit)));
    }

    @Override
    public Mono<Withdrawal> registerWithdrawal(Withdrawal withdrawal) {
        Mono<AccountDto> monoAccount = apiClients.findByAccount(withdrawal.getAccount().getId());

        return monoAccount
                .flatMap(acc -> {
                    if (acc.getBalance() >= withdrawal.getAmount()) {
                        acc.setBalance(acc.getBalance() - withdrawal.getAmount());
                    } else {
                        log.info("You don't have enough balance");
                    }
                    return apiClients.updateAccount(acc);
                }).flatMap(a -> withdrawalRepository.save(withdrawal));
    }

    @Override
    public Mono<Withdrawal> registerWithdrawalWithDebitCard(Withdrawal withdrawal) {
        Mono<DebitCardDto> monoDebitCard = apiClients.findByDebitCard(withdrawal.getAccount().getId());

        return monoDebitCard
                .flatMap(dc -> {
                    List<AccountDto> lstAccountPrincipal = dc.getAccounts().stream()
                            .filter(element -> element.isPrincipal() && element.getBalance() >= withdrawal.getAmount())
                            .collect(Collectors.toList());
                    AccountDto accountDtoPrincipal = dc.getAccounts().get(0);
                    if (accountDtoPrincipal.getBalance() >= withdrawal.getAmount()) {
                        log.info("the withdrawal will be made from the main account associated with the debit card");
                        accountDtoPrincipal.setBalance(accountDtoPrincipal.getBalance() - withdrawal.getAmount());
                        return apiClients.updateAccount(accountDtoPrincipal);
                    } else {
                        log.info("Availability should be analyzed in the following accounts associated with the card in the order in which they were associated with the debit card");
                        List<AccountDto> lstAccountNoPrincipal = dc.getAccounts().stream()
                                .filter(element -> !element.isPrincipal() && element.getBalance() >= withdrawal.getAmount())
                                .limit(1)
                                .collect(Collectors.toList());

                        if (lstAccountNoPrincipal.isEmpty()) {
                            log.info("associated non-main accounts do not have sufficient balance");
                        } else {
                            AccountDto accountDtoNoPrincipal = lstAccountNoPrincipal.get(0);
                            accountDtoNoPrincipal.setBalance(accountDtoNoPrincipal.getBalance() - withdrawal.getAmount());
                            return apiClients.updateAccount(accountDtoPrincipal);
                        }
                    }
                    return Mono.empty();
                }).flatMap(a -> withdrawalRepository.save(withdrawal));

    }

    @Override
    public Mono<Payment> registerPayment(Payment payment) {

        Mono<AccountDto> monoAccount = apiClients.findByAccount(payment.getAccount().getId());

        if (payment.getCredit() != null) {
            Mono<CreditDto> monoCredit = apiClients.findByCredit(payment.getCredit().getId());

            return monoAccount
                    .flatMap(acc -> monoCredit.flatMap(credit -> {
                        acc.setBalance(acc.getBalance() - payment.getAmount());
                        credit.setAmount(credit.getAmount() - payment.getAmount());
                        return apiClients.updateAccount(acc)
                                .flatMap(accUpdated -> apiClients.updateCredit(credit))
                                .flatMap(credUpdated -> paymentRepository.save(payment));
                    }));

        } else if (payment.getCreditCard() != null) {
            Mono<CreditCardDto> monoCreditCard = apiClients.findByCreditCard(payment.getCreditCard().getId());

            return monoAccount
                    .flatMap(acc -> monoCreditCard.flatMap(creditCard -> {
                        acc.setBalance(acc.getBalance() - payment.getAmount());
                        creditCard.setBalanceAmount(creditCard.getBalanceAmount() - payment.getAmount());
                        return apiClients.updateAccount(acc)
                                .flatMap(accUpdated -> apiClients.updateCreditCard(creditCard))
                                .flatMap(credUpdated -> paymentRepository.save(payment));
                    }));
        }
        return Mono.just(payment);
    }

    @Override
    public Mono<Payment> registerPaymentWithDebitCard(Payment payment) {
        Mono<DebitCardDto> monoDebitCard = apiClients.findByDebitCard(payment.getAccount().getId());

        return monoDebitCard
                .flatMap(dc -> {
                    List<AccountDto> lstAccountPrincipal = dc.getAccounts().stream()
                            .filter(element -> element.isPrincipal() && element.getBalance() >= payment.getAmount())
                            .collect(Collectors.toList());
                    AccountDto accountDtoPrincipal = dc.getAccounts().get(0);
                    if (payment.getCredit() != null) {
                        Mono<CreditDto> monoCredit = apiClients.findByCredit(payment.getCredit().getId());
                        if (accountDtoPrincipal.getBalance() >= payment.getAmount()) {
                            log.info("the withdrawal will be made from the main account associated with the debit card");
                            accountDtoPrincipal.setBalance(accountDtoPrincipal.getBalance() - payment.getAmount());
                            return apiClients.updateAccount(accountDtoPrincipal).flatMap(a -> paymentRepository.save(payment));
                        } else {
                            log.info("Availability should be analyzed in the following accounts associated with the card in the order in which they were associated with the debit card");
                            List<AccountDto> lstAccountNoPrincipal = dc.getAccounts().stream()
                                    .filter(element -> !element.isPrincipal() && element.getBalance() >= payment.getAmount())
                                    .limit(1)
                                    .collect(Collectors.toList());

                            if (lstAccountNoPrincipal.isEmpty()) {
                                log.info("associated non-main accounts do not have sufficient balance");
                            } else {
                                AccountDto accountDtoNoPrincipal = lstAccountNoPrincipal.get(0);
                                accountDtoNoPrincipal.setBalance(accountDtoNoPrincipal.getBalance() - payment.getAmount());
                                return apiClients.updateAccount(accountDtoPrincipal).flatMap(a -> paymentRepository.save(payment));
                            }
                        }
                    } else if (payment.getCreditCard() != null) {
                        Mono<CreditCardDto> monoCreditCard = apiClients.findByCreditCard(payment.getCreditCard().getId());
                        if (accountDtoPrincipal.getBalance() >= payment.getAmount()) {
                            log.info("the withdrawal will be made from the main account associated with the debit card");
                            accountDtoPrincipal.setBalance(accountDtoPrincipal.getBalance() - payment.getAmount());
                            return apiClients.updateAccount(accountDtoPrincipal).flatMap(a -> paymentRepository.save(payment));
                        } else {
                            log.info("Availability should be analyzed in the following accounts associated with the card in the order in which they were associated with the debit card");
                            List<AccountDto> lstAccountNoPrincipal = dc.getAccounts().stream()
                                    .filter(element -> !element.isPrincipal() && element.getBalance() >= payment.getAmount())
                                    .limit(1)
                                    .collect(Collectors.toList());

                            if (lstAccountNoPrincipal.isEmpty()) {
                                log.info("associated non-main accounts do not have sufficient balance");
                            } else {
                                AccountDto accountDtoNoPrincipal = lstAccountNoPrincipal.get(0);
                                accountDtoNoPrincipal.setBalance(accountDtoNoPrincipal.getBalance() - payment.getAmount());
                                return apiClients.updateAccount(accountDtoPrincipal).flatMap(a -> paymentRepository.save(payment));
                            }
                        }
                    }

                    return Mono.just(payment);
                });
    }

    private Mono<Transaction> validateSaveTransaction(Transaction transaction) {
        Mono<AccountDto> monoAccount = apiClients.findByAccount(transaction.getAccount().getId());
        Mono<Transaction> monoTransaction = Mono.just(transaction);

        return monoAccount
                .flatMap(acc -> {
                    transaction.setAccount(acc);
                    if (transaction.getTypeOperation().getCode().equalsIgnoreCase("W")) {
                        log.info("The type of operation is withdrawal");
                        if (acc.getAmount() > transaction.getAmount()) {
                            acc.setAmount(acc.getAmount() - transaction.getAmount());
                            return apiClients.updateAccount(acc).flatMap(ac -> {
                                return transactionRepository.save(transaction);
                            });
                        } else {
                            log.info("The amount is insufficient");
                        }

                    } else if (transaction.getTypeOperation().getCode().equalsIgnoreCase("D")) {
                        log.info("The type of operation is deposit");
                        acc.setAmount(acc.getAmount() + transaction.getAmount());
                        return apiClients.updateAccount(acc).flatMap(ac -> {
                            return transactionRepository.save(transaction);
                        });
                    } else if (transaction.getTypeOperation().getCode().equalsIgnoreCase("P")) {
                        log.info("The type of operation is payment");
                        acc.setAmount(acc.getAmount() + transaction.getAmount());
                    }
                    return Mono.just(transaction);
                });

    }

    @Override
    public Mono<Map<String, Object>> getMovements() {
        return null;
    }
}

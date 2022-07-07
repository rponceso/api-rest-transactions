package com.nttdata.apiresttransactions.service;


import com.nttdata.apiresttransactions.dto.AccountDto;
import com.nttdata.apiresttransactions.dto.CreditCardDto;
import com.nttdata.apiresttransactions.dto.CreditDto;
import com.nttdata.apiresttransactions.dto.DebitCardDto;
import reactor.core.publisher.Mono;

public interface APIClients {
    Mono<AccountDto> findByAccount(String idAccount);

    Mono<CreditDto> findByCredit(String idCredit);

    Mono<CreditCardDto> findByCreditCard(String idCreditCard);

    Mono<DebitCardDto> findByDebitCard(String idDebitCard);

    Mono<AccountDto> updateAccount(AccountDto accountDto);

    Mono<CreditDto> updateCredit(CreditDto creditDto);

    Mono<CreditCardDto> updateCreditCard(CreditCardDto creditCardDto);


}

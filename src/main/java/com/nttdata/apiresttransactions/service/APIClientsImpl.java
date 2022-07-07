package com.nttdata.apiresttransactions.service;

import com.nttdata.apiresttransactions.dto.AccountDto;
import com.nttdata.apiresttransactions.dto.CreditCardDto;
import com.nttdata.apiresttransactions.dto.CreditDto;
import com.nttdata.apiresttransactions.dto.DebitCardDto;
import com.nttdata.apiresttransactions.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class APIClientsImpl implements APIClients {

    private static final Logger log = LoggerFactory.getLogger(APIClientsImpl.class);

    @Autowired
    private WebClient webClient;

    @Value("${config.base.enpoint.accounts}")
    private String urlAccounts;

    @Value("${config.base.enpoint.debitcards}")
    private String urlDebitCards;

    @Value("${config.base.enpoint.credits}")
    private String urlCredits;

    @Value("${config.base.enpoint.creditcards}")
    private String urlCreditCards;

    @Override
    public Mono<AccountDto> findByAccount(String idAccount) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", idAccount);
        return webClient.get().uri(urlAccounts + "/{id}", params).accept(APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(AccountDto.class));
    }

    @Override
    public Mono<CreditDto> findByCredit(String idCredit) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", idCredit);
        return webClient.get().uri(urlCredits + "/{id}", params).accept(APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(CreditDto.class));
    }


    @Override
    public Mono<CreditCardDto> findByCreditCard(String idCreditCard) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", idCreditCard);
        return webClient.get().uri(urlCreditCards + "/{id}", params).accept(APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(CreditCardDto.class));
    }


    @Override
    public Mono<DebitCardDto> findByDebitCard(String idDebitCard) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", idDebitCard);
        return webClient.get().uri(urlDebitCards + "/{id}", params).accept(APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(DebitCardDto.class));
    }

    @Override
    public Mono<AccountDto> updateAccount(AccountDto accountDto) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", accountDto.getId());
        log.info("Update bank account details");
        return webClient.put()
                .uri(urlAccounts + "/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(accountDto), AccountDto.class)
                .retrieve()
                .bodyToMono(AccountDto.class);

    }

    @Override
    public Mono<CreditDto> updateCredit(CreditDto creditDto) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", creditDto.getId());
        return webClient.put().uri(urlCreditCards + "/{id}", params)
                .body(Mono.just(creditDto), CreditCardDto.class)
                .retrieve()
                .bodyToMono(CreditDto.class);
    }

    @Override
    public Mono<CreditCardDto> updateCreditCard(CreditCardDto creditCardDto) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", creditCardDto.getId());
        return webClient.put().uri(urlCreditCards + "/{id}", params)
                .body(Mono.just(creditCardDto), CreditCardDto.class)
                .retrieve()
                .bodyToMono(CreditCardDto.class);
    }


}

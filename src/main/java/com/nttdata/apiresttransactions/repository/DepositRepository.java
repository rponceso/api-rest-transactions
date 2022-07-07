package com.nttdata.apiresttransactions.repository;

import com.nttdata.apiresttransactions.model.Deposit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DepositRepository extends ReactiveMongoRepository<Deposit, String> {

}

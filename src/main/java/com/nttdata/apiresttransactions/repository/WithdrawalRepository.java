package com.nttdata.apiresttransactions.repository;

import com.nttdata.apiresttransactions.model.Withdrawal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface WithdrawalRepository extends ReactiveMongoRepository<Withdrawal, String> {

}

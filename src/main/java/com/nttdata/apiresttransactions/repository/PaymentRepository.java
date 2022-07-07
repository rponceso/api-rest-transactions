package com.nttdata.apiresttransactions.repository;

import com.nttdata.apiresttransactions.model.Payment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PaymentRepository extends ReactiveMongoRepository<Payment, String> {

}

package com.jovisco.spring6reactivemongo.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jovisco.spring6reactivemongo.domain.Customer;

import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

        Flux<Customer> findByName(String name);

}

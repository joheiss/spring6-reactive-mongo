package com.jovisco.spring6reactivemongo.services;

import com.jovisco.spring6reactivemongo.model.CustomerDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Flux<CustomerDTO> getAll();
    Mono<CustomerDTO> getById(String id);
    Mono<CustomerDTO> create(Mono<CustomerDTO> customerDto);
    Mono<CustomerDTO> create(CustomerDTO customerDto);
    Mono<CustomerDTO> update(String id, CustomerDTO customerDto);
    Mono<Void> delete(String id);

    Flux<CustomerDTO> findByName(String name);
}

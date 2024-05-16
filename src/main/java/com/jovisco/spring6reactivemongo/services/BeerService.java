package com.jovisco.spring6reactivemongo.services;

import com.jovisco.spring6reactivemongo.model.BeerDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {

    Flux<BeerDTO> getAll();
    Mono<BeerDTO> getById(String id);
    Mono<BeerDTO> create(Mono<BeerDTO> beerDto);
    Mono<BeerDTO> create(BeerDTO beerDto);
    Mono<BeerDTO> update(String id, BeerDTO beerDto);
    Mono<BeerDTO> patch(String id, BeerDTO beerDto);
    Mono<Void> delete(String id);

    Mono<BeerDTO> findFirstByBeerName(String beerName);
    Flux<BeerDTO> findByBeerStyle(String beerStyle);
}

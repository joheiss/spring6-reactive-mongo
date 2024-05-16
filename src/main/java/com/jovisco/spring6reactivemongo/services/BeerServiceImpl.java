package com.jovisco.spring6reactivemongo.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.jovisco.spring6reactivemongo.mappers.BeerMapper;
import com.jovisco.spring6reactivemongo.model.BeerDTO;
import com.jovisco.spring6reactivemongo.repositories.BeerRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    
    @Override
    public Flux<BeerDTO> getAll() {
        return beerRepository.findAll()
                .map(beerMapper::beerToDto);
    }    

    @Override
    public Mono<BeerDTO> getById(String id) {
        return beerRepository.findById(id)
            .map(beerMapper::beerToDto)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<BeerDTO> findFirstByBeerName(String beerName) {
        return beerRepository.findFirstByBeerName(beerName)
            .map(beerMapper::beerToDto);
    }

    @Override
    public Flux<BeerDTO> findByBeerStyle(String beerStyle) {
        return beerRepository.findByBeerStyle(beerStyle)
            .map(beerMapper::beerToDto);
    }

    @Override
    public Mono<BeerDTO> create(BeerDTO beerDTO) {
        return beerRepository.save(beerMapper.dtoToBeer(beerDTO))
                .map(beerMapper::beerToDto);
    }

    @Override
    public Mono<BeerDTO> create(Mono<BeerDTO> beerDto) {
        return beerDto.map(beerMapper::dtoToBeer)
            .flatMap(beerRepository::save)
            .map(beerMapper::beerToDto);
    }

    @Override
    public Mono<BeerDTO> update(String id, BeerDTO beerDTO) {
        return beerRepository.findById(id)
                .map(found -> {
                    //update properties
                    found.setBeerName(beerDTO.getBeerName());
                    found.setBeerStyle(beerDTO.getBeerStyle());
                    found.setPrice(beerDTO.getPrice());
                    found.setUpc(beerDTO.getUpc());
                    found.setQuantityOnHand(beerDTO.getQuantityOnHand());

                    return found;
                }).flatMap(beerRepository::save)
                .map(beerMapper::beerToDto);
    }

    @Override
    public Mono<BeerDTO> patch(String id, BeerDTO beerDTO) {
        return beerRepository.findById(id)
            .map(found -> {
                if(StringUtils.hasText(beerDTO.getBeerName())){
                    found.setBeerName(beerDTO.getBeerName());
                }
                if(StringUtils.hasText(beerDTO.getBeerStyle())){
                    found.setBeerStyle(beerDTO.getBeerStyle());
                }
                if(beerDTO.getPrice() != null){
                    found.setPrice(beerDTO.getPrice());
                }
                if(StringUtils.hasText(beerDTO.getUpc())){
                    found.setUpc(beerDTO.getUpc());
                }
                if(beerDTO.getQuantityOnHand() != null){
                    found.setQuantityOnHand(beerDTO.getQuantityOnHand());
                }
                return found;
            })
            .flatMap(beerRepository::save)
            .map(beerMapper::beerToDto);
    }    

    @Override
    public Mono<Void> delete(String id) {
        return beerRepository.deleteById(id);
    }

}

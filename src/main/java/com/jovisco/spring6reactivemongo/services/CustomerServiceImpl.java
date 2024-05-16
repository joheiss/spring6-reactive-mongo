package com.jovisco.spring6reactivemongo.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.jovisco.spring6reactivemongo.mappers.CustomerMapper;
import com.jovisco.spring6reactivemongo.model.CustomerDTO;
import com.jovisco.spring6reactivemongo.repositories.CustomerRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    
    @Override
    public Flux<CustomerDTO> getAll() {
        return customerRepository.findAll()
                .map(customerMapper::customerToDto);
    }    

    @Override
    public Mono<CustomerDTO> getById(String id) {
        return customerRepository.findById(id)
            .map(customerMapper::customerToDto)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<CustomerDTO> findByName(String name) {
        return customerRepository.findByName(name)
            .map(customerMapper::customerToDto);
    }

    @Override
    public Mono<CustomerDTO> create(CustomerDTO customerDTO) {
        return customerRepository.save(customerMapper.dtoToCustomer(customerDTO))
                .map(customerMapper::customerToDto);
    }

    @Override
    public Mono<CustomerDTO> create(Mono<CustomerDTO> customerDto) {
        return customerDto.map(customerMapper::dtoToCustomer)
            .flatMap(customerRepository::save)
            .map(customerMapper::customerToDto);
    }

    @Override
    public Mono<CustomerDTO> update(String id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .map(found -> {
                    //update properties
                    found.setName(customerDTO.getName());
                    return found;
                }).flatMap(customerRepository::save)
                .map(customerMapper::customerToDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return customerRepository.deleteById(id);
    }

}

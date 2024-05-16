package com.jovisco.spring6reactivemongo.webfn;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;

import com.jovisco.spring6reactivemongo.model.CustomerDTO;
import com.jovisco.spring6reactivemongo.services.CustomerService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class CustomerHandler {

    private final CustomerService customerService;
    private final Validator validator;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        
        Flux<CustomerDTO> flux;

        if (request.queryParam("name").isPresent()){
            flux = customerService.findByName(request.queryParam("name").get());
        } else {
            flux = customerService.getAll();
        }

        return ServerResponse.ok()
                .body(flux, CustomerDTO.class);        
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        // get id from path variable
        var id = request.pathVariable("id");

        return ServerResponse.ok()
            .body(customerService.getById(id), CustomerDTO.class);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        // get body from request
        // var body = request.bodyToMono(CustomerDTO.class);

        return request.bodyToMono(CustomerDTO.class)
            .doOnNext(this::validate)
            .flatMap(body -> customerService.create(body))
            .flatMap(dto -> ServerResponse
                .created(UriComponentsBuilder
                    .fromUriString(CustomerRouterConfig.CUSTOMER_ID_PATH)
                    .build(dto.getId()))
                .build()
            );
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        // get id from path variable
        var id = request.pathVariable("id");

        return customerService.getById(id)
            .flatMap(found -> request.bodyToMono(CustomerDTO.class))
            .doOnNext(this::validate)
            .flatMap(dto -> customerService.update(id, dto))
            .flatMap(updated -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        // get id from path variable
        var id = request.pathVariable("id");

        return customerService.getById(id)
            .flatMap(found -> customerService.delete(id))
            .then(ServerResponse.noContent().build());
    }

    private void validate(CustomerDTO customerDTO) {
        var errors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
        validator.validate(customerDTO, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }
}

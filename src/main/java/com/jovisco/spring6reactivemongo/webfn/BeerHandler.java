package com.jovisco.spring6reactivemongo.webfn;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;

import com.jovisco.spring6reactivemongo.model.BeerDTO;
import com.jovisco.spring6reactivemongo.services.BeerService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class BeerHandler {

    private final BeerService beerService;
    private final Validator validator;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        
        Flux<BeerDTO> flux;

        if (request.queryParam("beerStyle").isPresent()){
            flux = beerService.findByBeerStyle(request.queryParam("beerStyle").get());
        } else {
            flux = beerService.getAll();
        }

        return ServerResponse.ok()
                .body(flux, BeerDTO.class);        
        // return ServerResponse.ok()
        //     .body(beerService.getAll(), BeerDTO.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        // get id from path variable
        var id = request.pathVariable("id");

        return ServerResponse.ok()
            .body(beerService.getById(id), BeerDTO.class);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        // get body from request
        // var body = request.bodyToMono(BeerDTO.class);

        return request.bodyToMono(BeerDTO.class)
            .doOnNext(this::validate)
            .flatMap(body -> beerService.create(body))
            .flatMap(dto -> ServerResponse
                .created(UriComponentsBuilder
                    .fromUriString(BeerRouterConfig.BEER_ID_PATH)
                    .build(dto.getId()))
                .build()
            );
    }

        public Mono<ServerResponse> update(ServerRequest request) {
        // get id from path variable
        var id = request.pathVariable("id");

        return beerService.getById(id)
            .flatMap(found -> request.bodyToMono(BeerDTO.class))
            .doOnNext(this::validate)
            .flatMap(dto -> beerService.update(id, dto))
            .flatMap(updated -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patch(ServerRequest request) {
        // get id from path variable
        var id = request.pathVariable("id");

        return beerService.getById(id)
            .flatMap(found -> request.bodyToMono(BeerDTO.class))
            .doOnNext(this::validate)
            .flatMap(dto -> beerService.patch(id, dto))
            .flatMap(updated -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        // get id from path variable
        var id = request.pathVariable("id");

        return beerService.getById(id)
            .flatMap(found -> beerService.delete(id))
            .then(ServerResponse.noContent().build());
    }

    private void validate(BeerDTO beerDTO) {
        var errors = new BeanPropertyBindingResult(beerDTO, "beerDTO");
        validator.validate(beerDTO, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }
}

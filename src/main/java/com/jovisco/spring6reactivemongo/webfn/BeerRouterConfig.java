package com.jovisco.spring6reactivemongo.webfn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@RequiredArgsConstructor
@Configuration
public class BeerRouterConfig {

    public static final String BEER_PATH = "/api/v3/beers";
    public static final String BEER_ID_PATH = BEER_PATH + "/{id}";

    private final BeerHandler handler;

    @Bean
    public RouterFunction<ServerResponse> beerRoutes() {
        return route()
            .GET(BEER_PATH, accept(APPLICATION_JSON), handler::getAll)
            .GET(BEER_ID_PATH, accept(APPLICATION_JSON), handler::getById)
            .POST(BEER_PATH, accept(APPLICATION_JSON), handler::create)
            .POST(BEER_PATH, accept(APPLICATION_JSON), handler::create)
            .PUT(BEER_ID_PATH, accept(APPLICATION_JSON), handler::update)
            .PATCH(BEER_ID_PATH, accept(APPLICATION_JSON), handler::patch)
            .DELETE(BEER_ID_PATH, accept(APPLICATION_JSON), handler::delete)
            .build();
    }
}

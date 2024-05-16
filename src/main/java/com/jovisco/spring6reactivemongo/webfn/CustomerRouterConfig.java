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
public class CustomerRouterConfig {

    public static final String CUSTOMER_PATH = "/api/v3/customers";
    public static final String CUSTOMER_ID_PATH = CUSTOMER_PATH + "/{id}";

    private final CustomerHandler handler;

    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return route()
            .GET(CUSTOMER_PATH, accept(APPLICATION_JSON), handler::getAll)
            .GET(CUSTOMER_ID_PATH, accept(APPLICATION_JSON), handler::getById)
            .POST(CUSTOMER_PATH, accept(APPLICATION_JSON), handler::create)
            .POST(CUSTOMER_PATH, accept(APPLICATION_JSON), handler::create)
            .PUT(CUSTOMER_ID_PATH, accept(APPLICATION_JSON), handler::update)
            .DELETE(CUSTOMER_ID_PATH, accept(APPLICATION_JSON), handler::delete)
            .build();
    }
}

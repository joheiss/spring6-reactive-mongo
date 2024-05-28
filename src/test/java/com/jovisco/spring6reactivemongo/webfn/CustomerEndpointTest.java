package com.jovisco.spring6reactivemongo.webfn;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.jovisco.spring6reactivemongo.domain.Customer;
import com.jovisco.spring6reactivemongo.mappers.CustomerMapperImpl;
import com.jovisco.spring6reactivemongo.model.CustomerDTO;
import com.jovisco.spring6reactivemongo.services.CustomerService;

import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.equalTo;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class CustomerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CustomerService customerService;
    
    @Test
    @Order(100)
    void testGetAllCustomers() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().jsonPath("$.size()", hasSize(greaterThan(1)));
    }

    @Test
    @Order(100)
    void testGetById() {
        var customerDTO = getSavedTestCustomer();

        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, customerDTO.getId())
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(CustomerDTO.class);
    }

    @Test
    @Order(100)
    void testGetByIdNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, 999)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(100)
    void testListCustomersByName() {
        final String CUSTOMER_NAME = "TEST";
        CustomerDTO testDto = getSavedTestCustomer();
        testDto.setName(CUSTOMER_NAME);

        //create test data
        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .body(Mono.just(testDto), CustomerDTO.class)
            .exchange();

        webTestClient
            .mutateWith(mockOAuth2Login())
            .get()
            .uri(UriComponentsBuilder
                .fromPath(CustomerRouterConfig.CUSTOMER_PATH)
                .queryParam("name", CUSTOMER_NAME).build().toUri()
            )
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    @Test
    @Order(200)
    void testCreateCustomer() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .body(Mono.just(getTestCustomer()), CustomerDTO.class)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists("Location");
    }

    @Test
    @Order(200)
    void testCreateCustomerWithValidationError() {
        var testCustomer = getTestCustomer();
        testCustomer.setName("");

        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .body(Mono.just(testCustomer), CustomerDTO.class)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(300)
    void testUpdateCustomer() {

        var customerDTO = getSavedTestCustomer();

        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(CustomerRouterConfig.CUSTOMER_ID_PATH, customerDTO.getId())
            .body(Mono.just(customerDTO), CustomerDTO.class)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(300)
    void testUpdateCustomerNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(CustomerRouterConfig.CUSTOMER_ID_PATH, 999)
            .body(Mono.just(getTestCustomer()), CustomerDTO.class)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(300)
    void testUpdateCustomerBadRequest() {
        var testCustomer = getSavedTestCustomer();
        testCustomer.setName("");

        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(CustomerRouterConfig.CUSTOMER_ID_PATH, testCustomer.getId())
            .body(Mono.just(testCustomer), CustomerDTO.class)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(900)
    void testDeleteCustomer() {
       var customerDTO = getSavedTestCustomer();

        webTestClient
            .mutateWith(mockOAuth2Login())
            .delete()
            .uri(CustomerRouterConfig.CUSTOMER_ID_PATH, customerDTO.getId())
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(900)
    void testDeleteNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .delete()
            .uri(CustomerRouterConfig.CUSTOMER_ID_PATH, 999)
            .exchange()
            .expectStatus().isNotFound();
    }

    public CustomerDTO getSavedTestCustomer(){
        var customerDTOFluxExchangeResult = webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .body(Mono.just(getTestCustomer()), CustomerDTO.class)
            .header("Content-Type", "application/json")
            .exchange()
            .returnResult(CustomerDTO.class);

        var location = customerDTOFluxExchangeResult.getResponseHeaders().get("Location");
        System.out.println(location);

        return webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .exchange()
            .returnResult(CustomerDTO.class)
            .getResponseBody()
            .blockFirst();
    }

    public static Customer getTestCustomer() {
        return Customer.builder()
            .name("Saufkopf AG")
            .build();
    }   
    
    public static CustomerDTO getTestCustomerDto() {
        return new CustomerMapperImpl().customerToDto(getTestCustomer());
    }

    public CustomerDTO getCreatedCustomerDto() {
        return customerService.create(Mono.just(getTestCustomerDto())).block();
    }
}
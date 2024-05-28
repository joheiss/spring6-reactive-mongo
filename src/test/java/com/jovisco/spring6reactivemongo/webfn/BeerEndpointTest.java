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

import com.jovisco.spring6reactivemongo.model.BeerDTO;
import com.jovisco.spring6reactivemongo.services.BeerServiceImplTest;

import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.equalTo;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class BeerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(100)
    void testGetAllBeers() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(BeerRouterConfig.BEER_PATH)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().jsonPath("$.size()", hasSize(greaterThan(1)));
    }

    @Test
    @Order(100)
    void testGetById() {
        var beerDTO = getSavedTestBeer();

        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(BeerRouterConfig.BEER_ID_PATH, beerDTO.getId())
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(BeerDTO.class);
    }

    @Test
    @Order(100)
    void testGetByIdNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(BeerRouterConfig.BEER_ID_PATH, 999)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(100)
    void testListBeersByStyle() {
        final String BEER_STYLE = "TEST";
        BeerDTO testDto = getSavedTestBeer();
        testDto.setBeerStyle(BEER_STYLE);

        //create test data
        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(BeerRouterConfig.BEER_PATH)
            .body(Mono.just(testDto), BeerDTO.class)
            .exchange();

        webTestClient
            .mutateWith(mockOAuth2Login())
            .get()
            .uri(UriComponentsBuilder
                .fromPath(BeerRouterConfig.BEER_PATH)
                .queryParam("beerStyle", BEER_STYLE).build().toUri()
            )
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    @Test
    @Order(200)
    void testCreateBeer() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(BeerRouterConfig.BEER_PATH)
            .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists("Location");
    }

    @Test
    @Order(200)
    void testCreateBeerWithValidationError() {
        var testBeer = BeerServiceImplTest.getTestBeer();
        testBeer.setBeerName("");

        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(BeerRouterConfig.BEER_PATH)
            .body(Mono.just(testBeer), BeerDTO.class)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(300)
    void testUpdateBeer() {

        var beerDTO = getSavedTestBeer();

        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(BeerRouterConfig.BEER_ID_PATH, beerDTO.getId())
            .body(Mono.just(beerDTO), BeerDTO.class)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(300)
    void testUpdateBeerNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(BeerRouterConfig.BEER_ID_PATH, 999)
            .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(300)
    void testUpdateBeerBadRequest() {
        var testBeer = getSavedTestBeer();
        testBeer.setBeerName("");

        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(BeerRouterConfig.BEER_ID_PATH, testBeer.getId())
            .body(Mono.just(testBeer), BeerDTO.class)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(300)
    void testPatchIdFound() {
        var beerDTO = getSavedTestBeer();

        webTestClient
            .mutateWith(mockOAuth2Login())
            .patch()
            .uri(BeerRouterConfig.BEER_ID_PATH, beerDTO.getId())
            .body(Mono.just(beerDTO), BeerDTO.class)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(300)
    void testPatchIdNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .patch()
            .uri(BeerRouterConfig.BEER_ID_PATH, 999)
            .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(900)
    void testDeleteBeer() {
       var beerDTO = getSavedTestBeer();

        webTestClient
            .mutateWith(mockOAuth2Login())        
            .delete()
            .uri(BeerRouterConfig.BEER_ID_PATH, beerDTO.getId())
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(900)
    void testDeleteNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .delete()
            .uri(BeerRouterConfig.BEER_ID_PATH, 999)
            .exchange()
            .expectStatus().isNotFound();
    }

    public BeerDTO getSavedTestBeer(){
        var beerDTOFluxExchangeResult = webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
            .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
            .header("Content-Type", "application/json")
            .exchange()
            .returnResult(BeerDTO.class);

        var location = beerDTOFluxExchangeResult.getResponseHeaders().get("Location");
        System.out.println(location);

        return webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(BeerRouterConfig.BEER_PATH)
            .exchange()
            .returnResult(BeerDTO.class)
            .getResponseBody()
            .blockFirst();
    }

}
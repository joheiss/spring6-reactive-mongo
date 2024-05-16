package com.jovisco.spring6reactivemongo.services;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jovisco.spring6reactivemongo.domain.Beer;
import com.jovisco.spring6reactivemongo.mappers.BeerMapper;
import com.jovisco.spring6reactivemongo.mappers.BeerMapperImpl;
import com.jovisco.spring6reactivemongo.model.BeerDTO;

import reactor.core.publisher.Mono;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Autowired
    BeerMapper beerMapper;
    
    BeerDTO beerDTO;

    @BeforeEach
    void setup() {
        beerDTO = beerMapper.beerToDto(getTestBeer());
    }

    @Test
    void testGetAll() {

        var atomicBoolean = new AtomicBoolean(false);

        var found = beerService.getAll();
        var count = found.count().block();
        System.out.println("Beers found: " + count);

        found.subscribe(dto -> {
            System.out.println(dto);
            System.out.flush();
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);

        assertThat(found.count().block()).isGreaterThan(0);

    }

    @Test
    void testCreate() throws InterruptedException {

        var atomicBoolean = new AtomicBoolean(false);

        Mono<BeerDTO> created = beerService.create(Mono.just(beerDTO));
        created.subscribe(dto -> {
            System.out.println("*** CREATED KEY ***");
            System.out.println(dto.getId());
            System.out.flush();
            atomicBoolean.set(true);
        });

        // waits for the atomicBoolean to become true -> when the db operation is finished
        await().untilTrue(atomicBoolean);

        // make sure the database has enough time to finish the creation before the test process is finished
        // Thread.sleep(1000);
    }

    @Test
    @DisplayName("Test create a beer using a subscriber")
    void testCreateUseSubscriber() {

        var atomicBoolean = new AtomicBoolean(false);
        var atomicDto = new AtomicReference<BeerDTO>();

        var created = beerService.create(Mono.just(beerDTO));
        
        created.subscribe(dto -> {
            System.out.println("*** CREATED KEY ***");
            System.out.println(dto.getId());
            System.out.flush();
            atomicBoolean.set(true);
            atomicDto.set(dto);
        });

        // waits for the atomicBoolean to become true -> when the db operation is finished
        await().untilTrue(atomicBoolean);

        BeerDTO saved = atomicDto.get();
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotEmpty();
    }

    @Test
    @DisplayName("Test create a beer using block")
    void testCreateBeerUseBlock() {

        var saved = beerService.create(Mono.just(getTestBeerDto())).block();

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotEmpty();
    }

    @Test
    @DisplayName("Test update a beer using block")
    void testUpdateUsingBlock() {

        final String newName = "**UPDATED**";  
        var created = getCreatedBeerDto();
        created.setBeerName(newName);

       var updated = beerService.create(Mono.just(created)).block();

        //verify exists in db
        var found = beerService.getById(updated.getId()).block();

        assertThat(found.getBeerName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test update a beer using reactive streams")
    void testUpdateUsingStreams() {

        final String newName = "**UPDATED AGAIN**";  

        var atomicDto = new AtomicReference<BeerDTO>();

        beerService.create(Mono.just(getTestBeerDto()))
                .map(created -> {
                    created.setBeerName(newName);
                    return created;
                })
                .flatMap(beerService::create) 
                .flatMap(updated -> beerService.getById(updated.getId())) 
                .subscribe(found -> {
                    atomicDto.set(found);
                });

        await().until(() -> atomicDto.get() != null);
        assertThat(atomicDto.get().getBeerName()).isEqualTo(newName);
    }
    

    @Test
    void testDeleteBeer() {

       var toBeDeleted = getCreatedBeerDto();

        beerService.delete(toBeDeleted.getId()).block();

        var expectedEmptyBeerMono = beerService.getById(toBeDeleted.getId());
        var emptyBeer = expectedEmptyBeerMono.block();
        assertThat(emptyBeer).isNull();

        // assertThatExceptionOfType(ResponseStatusException.class)
        //     .isThrownBy(() -> beerService.getById(toBeDeleted.getId()));
    }

    @Test
    void testFindFirstByBeerName() {
        var created = getCreatedBeerDto();

        var atomicBoolean = new AtomicBoolean(false);

        var found = beerService.findFirstByBeerName(created.getBeerName());
        found.subscribe(dto -> {
            System.out.println(dto);
            System.out.flush();
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void testFindByBeerStyle() {
        var created = getCreatedBeerDto();

        var atomicBoolean = new AtomicBoolean(false);

        var found = beerService.findByBeerStyle(created.getBeerStyle());
        found.subscribe(dto -> {
            System.out.println(dto);
            System.out.flush();
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void testGetById() {

    }

    public static Beer getTestBeer() {
        return Beer.builder()
            .beerName("Paulaner Hell")
            .beerStyle("PALE ALE")
            .price(new BigDecimal("9.87"))
            .quantityOnHand(23)
            .upc("1234567")
            .build();
    }   
    
    public static BeerDTO getTestBeerDto() {
        return new BeerMapperImpl().beerToDto(getTestBeer());
    }

    public BeerDTO getCreatedBeerDto() {
        return beerService.create(Mono.just(getTestBeerDto())).block();
    }
}

package com.jovisco.spring6reactivemongo.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jovisco.spring6reactivemongo.domain.Beer;
import com.jovisco.spring6reactivemongo.domain.Customer;
import com.jovisco.spring6reactivemongo.repositories.BeerRepository;
import com.jovisco.spring6reactivemongo.repositories.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        beerRepository.deleteAll()
            .doOnSuccess(success -> loadBeers())        
            .subscribe();
        customerRepository.deleteAll()
            .doOnSuccess(success -> loadCustomers())        
            .subscribe();

    }

    private void loadBeers() {
        beerRepository
            .saveAll(getBeers())
            .subscribe(
                beer -> log.info("Data loaded successfully: " + beer.getBeerName()),
                error -> log.error("Error while loading data", error)
            );
    }

    private List<Beer> getBeers() {

        Beer beer1 = Beer.builder()
            .beerName("Erdinger Weizen")
            .beerStyle("WHEAT")
            .upc("12341")
            .price(new BigDecimal("9.87"))
            .quantityOnHand(121)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Beer beer2 = Beer.builder()
            .beerName("Salvator Bock")
            .beerStyle("STOUT")
            .upc("12342")
            .price(new BigDecimal("1.23"))
            .quantityOnHand(122)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Beer beer3 = Beer.builder()
            .beerName("Paulaner Hell")
            .beerStyle("PALE_ALE")
            .upc("12343")
            .price(new BigDecimal("12.34"))
            .quantityOnHand(123)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        return Arrays.asList(beer1, beer2, beer3);
    }

    private void loadCustomers() {
        customerRepository
            .saveAll(getCustomers())
            .subscribe(
                customer -> log.info("Data loaded successfully: " + customer.getName()),
                error -> log.error("Error while loading data", error)
            );
    }

    private List<Customer> getCustomers() {

        Customer customer1 = Customer.builder()
            .name("Gurgel OHG")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Customer customer2 = Customer.builder()
            .name("Gluck & Gluck AG")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Customer customer3 = Customer.builder()
            .name("Lall GmbH")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        return Arrays.asList(customer1, customer2, customer3);
    }

}

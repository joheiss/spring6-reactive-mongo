package com.jovisco.spring6reactivemongo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.jovisco.spring6reactivemongo.domain.Beer;
import com.jovisco.spring6reactivemongo.model.BeerDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeerMapper {
    Beer dtoToBeer(BeerDTO beerDTO);
    BeerDTO beerToDto(Beer beer);
}

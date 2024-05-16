package com.jovisco.spring6reactivemongo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.jovisco.spring6reactivemongo.domain.Customer;
import com.jovisco.spring6reactivemongo.model.CustomerDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    Customer dtoToCustomer(CustomerDTO customerDTO);
    CustomerDTO customerToDto(Customer customer);
}

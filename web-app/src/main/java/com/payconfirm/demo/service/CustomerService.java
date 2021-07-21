package com.payconfirm.demo.service;

import com.payconfirm.demo.database.Customer;
import com.payconfirm.demo.model.CreateAliasResponse;

import java.util.List;

public interface CustomerService {
    void create(CreateAliasResponse request);

    void update(Integer id, CreateAliasResponse request);

    void delete(Integer id);

    List<Customer> findAll();

    Customer findById(Integer id);

    Customer findByAlias(String alias);

}

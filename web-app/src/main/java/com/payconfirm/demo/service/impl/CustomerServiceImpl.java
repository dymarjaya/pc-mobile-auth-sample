package com.payconfirm.demo.service.impl;

import com.payconfirm.demo.database.Customer;
import com.payconfirm.demo.model.CreateAliasResponse;
import com.payconfirm.demo.repository.CustomerRepository;
import com.payconfirm.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void create(CreateAliasResponse request) {
        Customer customer = new Customer();
        customer.setAlias(request.getAlias());
        customer.setActivation_code(request.getActivation_code());
        customerRepository.save(customer);
    }

    @Override
    public void update(Integer id, CreateAliasResponse request) {
        Customer customer = findById(id);
        customer.setActivation_code(request.getActivation_code());
        customerRepository.save(customer);
    }

    @Override
    public void delete(Integer id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public Customer findByAlias(String alias) {
        return customerRepository.findByAlias(alias);
    }

}

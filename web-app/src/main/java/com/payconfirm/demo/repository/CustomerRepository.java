package com.payconfirm.demo.repository;

import com.payconfirm.demo.database.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Customer findByAlias(String alias);

}
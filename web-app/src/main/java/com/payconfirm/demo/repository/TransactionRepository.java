package com.payconfirm.demo.repository;

import com.payconfirm.demo.database.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Transaction findByTransactionid(String transactionid);

    Transaction findByUserid(String userid);

    Transaction findTopByOrderByIdDesc();

}

package com.payconfirm.demo.service;

import com.payconfirm.demo.database.Transaction;

public interface TransactionService {

    Transaction findByTransactionid(String transactionid);

    Transaction findTopByOrderByIdDesc();

}

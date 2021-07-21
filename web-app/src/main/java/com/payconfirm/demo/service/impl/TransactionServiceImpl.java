package com.payconfirm.demo.service.impl;

import com.payconfirm.demo.database.Transaction;
import com.payconfirm.demo.repository.TransactionRepository;
import com.payconfirm.demo.service.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;

    @Override
    public Transaction findByTransactionid (String transactionid) {
        return transactionRepository.findByTransactionid(transactionid);
    }

    @Override
    public Transaction findTopByOrderByIdDesc() {
        return transactionRepository.findTopByOrderByIdDesc();
    }

}

package ru.xplago.transactionservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.transactionservice.entities.Transaction;
import ru.xplago.transactionservice.exceptions.NotFoundException;
import ru.xplago.transactionservice.repositories.TransactionRepository;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TransactionService {
    private TransactionRepository transactionRepository;

    public Transaction findById(String id) {
        return transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction with id " + id + " not found"));
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findAllByAccountId(String accountId) {
        return transactionRepository.findAllBySenderAccountIdOrReceiverAccountIdOrderByModifiedDesc(accountId, accountId);
    }
}

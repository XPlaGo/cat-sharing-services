package ru.xplago.transactionservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xplago.transactionservice.entities.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllBySenderAccountIdOrReceiverAccountIdOrderByModifiedDesc(String senderAccountId, String receiverAccountId);
}

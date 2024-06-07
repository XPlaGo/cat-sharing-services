package ru.xplago.paymentservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xplago.paymentservice.entities.PaymentAccount;

import java.util.List;
import java.util.Optional;

public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, String> {
    Optional<PaymentAccount> findByUserId(Long userId);
    List<PaymentAccount> findAllByUserId(Long userId);
    Optional<PaymentAccount> findByIdAndUserId(String id, Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByIdAndUserId(String id, Long userId);
}

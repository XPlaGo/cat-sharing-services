package ru.xplago.paymentservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.auth.UserInfo;
import ru.xplago.common.grpc.payment.AccountInfo;
import ru.xplago.paymentservice.converters.UserInfoConverter;
import ru.xplago.paymentservice.entities.PaymentAccount;
import ru.xplago.paymentservice.exceptions.NotFoundException;
import ru.xplago.paymentservice.exceptions.PaymentAccountAlreadyExistsException;
import ru.xplago.paymentservice.repositories.PaymentAccountRepository;
import ru.xplago.paymentservice.services.dto.CreatePaymentAccountDto;
import ru.xplago.paymentservice.services.dto.PublicAccountInfoDto;
import ru.xplago.paymentservice.services.dto.UserInfoDto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentAccountService {

    private PaymentAccountRepository paymentAccountRepository;
    private UserService userService;

    public PaymentAccount save(PaymentAccount paymentAccount) {
        return paymentAccountRepository.save(paymentAccount);
    }

    public PaymentAccount create(CreatePaymentAccountDto dto) {

        if (existsByUserId(dto.getUserId())) {
            throw new PaymentAccountAlreadyExistsException("Payment account already exists for user with id " + dto.getUserId());
        }

        return paymentAccountRepository.save(
                PaymentAccount.builder()
                        .amount(BigDecimal.ZERO)
                        .userId(dto.getUserId())
                        .created(Timestamp.from(Instant.now()))
                        .modified(Timestamp.from(Instant.now()))
                        .build()
        );
    }

    public PaymentAccount findById(String id) {
        return paymentAccountRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Payment account with id \"" + id + "\" not found")
        );
    }

    public PaymentAccount findByIdAndUserId(String id, Long userId) {
        return paymentAccountRepository.findByIdAndUserId(id, userId).orElseThrow(
                () -> new NotFoundException("Payment account with id \"" + id + "\" not found")
        );
    }

    public PaymentAccount findByUserId(Long userId) {
        return paymentAccountRepository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException("Payment account with user id \"" + userId + "\" not found")
        );
    }

    public List<PaymentAccount> findAllByUserId(Long userId) {
        return paymentAccountRepository.findAllByUserId(userId);
    }

    public boolean existsByUserId(Long userId) {
        return paymentAccountRepository.existsByUserId(userId);
    }

    public boolean existsByIdAndUserId(String id, Long userId) {
        return paymentAccountRepository.existsByIdAndUserId(id, userId);
    }

    public PublicAccountInfoDto getByEmail(String email) {
        UserInfo userInfo = userService.getUserByEmail(email);
        PaymentAccount accountInfo = findByUserId(userInfo.getId());
        return PublicAccountInfoDto.builder()
                .id(accountInfo.getId())
                .user(UserInfoConverter.convert(userInfo))
                .build();
    }
}

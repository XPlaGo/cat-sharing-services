package ru.xplago.authservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.authservice.exceptions.VerificationCodeException;
import ru.xplago.authservice.models.VerificationCodeModel;
import ru.xplago.authservice.properties.VerificationCodeProperties;
import ru.xplago.authservice.repositories.VerificationCodeRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class VerificationCodeService {

    private VerificationCodeRepository verificationCodeRepository;
    private MailService mailService;
    private VerificationCodeProperties verificationCodeProperties;

    public VerificationCodeModel generateAndSendVerificationCode(String email) {

        Optional<VerificationCodeModel> verificationCodeModel = verificationCodeRepository.findById(email);
        verificationCodeModel.ifPresent((model) -> {
            if (!canRefreshVerificationToken(model)) throw new VerificationCodeException("Unable to refresh verification token");
        });

        VerificationCodeModel model = verificationCodeRepository.save(new VerificationCodeModel(
                email,
                VerificationCodeService.generateVerificationCode(),
                new Date(new Date().getTime() + verificationCodeProperties.getExpirationSec() * 1000L),
                new Date()
        ));

        mailService.sendVerificationCode(model.getEmail(), model.getVerificationCode());

        return model;
    }

    public VerificationCodeModel checkAndDeleteVerificationCode(String email, String verificationCode) {
        Optional<VerificationCodeModel> verificationCodeModel = verificationCodeRepository.findById(email);
        VerificationCodeModel model = verificationCodeModel
                .orElseThrow(() -> new VerificationCodeException("Verification code not found"));

        if (!model.getVerificationCode().equals(verificationCode)) {
            throw new VerificationCodeException("Invalid verification code");
        }

        if (!validateVerificationCodeExpiration(model)) {
            throw new VerificationCodeException("Verification code expired");
        }

        verificationCodeRepository.deleteById(email);

        return model;
    }

    private boolean validateVerificationCodeExpiration(VerificationCodeModel model) {
        return new Date().before(model.getExpiryDate());
    }

    private boolean canRefreshVerificationToken(VerificationCodeModel model) {
        return new Date().after(new Date(model.getCreateDate().getTime() + verificationCodeProperties.getRefreshSec() * 1000L));
    }

    static public String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }
}

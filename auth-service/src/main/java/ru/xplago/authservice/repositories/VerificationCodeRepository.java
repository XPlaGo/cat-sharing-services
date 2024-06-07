package ru.xplago.authservice.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.xplago.authservice.models.VerificationCodeModel;

public interface VerificationCodeRepository extends CrudRepository<VerificationCodeModel, String> {
}

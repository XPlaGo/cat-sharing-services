package ru.xplago.catservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.catservice.entities.Owner;
import ru.xplago.catservice.exceptions.NotFoundException;
import ru.xplago.catservice.exceptions.OwnerAlreadyExistsException;
import ru.xplago.catservice.repositories.OwnerRepository;
import ru.xplago.catservice.services.dto.CreateOwnerDto;
import ru.xplago.common.grpc.validation.validators.ModelValidator;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OwnerService {
    private OwnerRepository ownerRepository;
    private ModelValidator validator;

    public Owner createOwner(CreateOwnerDto dto) {
        validator.validate(dto);

        if (ownerRepository.existsByUserId(dto.getUserId())) {
            throw new OwnerAlreadyExistsException("Owner for user with id " + dto.getUserId() + " already exists");
        }

        return ownerRepository.save(Owner.builder()
                .userId(dto.getUserId())
                .cats(new ArrayList<>())
                .rentedCats(new ArrayList<>())
                .created(Timestamp.from(Instant.now()))
                .modified(Timestamp.from(Instant.now()))
                .build()
        );
    }

    public Owner save(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Owner findById(Long id) {
        return ownerRepository.findById(id).orElseThrow(() -> new NotFoundException("Owner with id " + id + " not found"));
    }

    public boolean existsById(Long id) {
        return ownerRepository.existsById(id);
    }

    public Owner findByUserId(Long id) {
        return ownerRepository.findByUserId(id).orElseThrow(() -> new NotFoundException("Owner with user id " + id + " not found"));
    }

    public boolean existsByUserId(Long id) {
        return ownerRepository.existsByUserId(id);
    }

    public void deleteByUserId(Long id) {
        ownerRepository.deleteByUserId(id);
    }
}

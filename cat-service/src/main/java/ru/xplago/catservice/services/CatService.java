package ru.xplago.catservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.catservice.entities.Cat;
import ru.xplago.catservice.entities.Owner;
import ru.xplago.catservice.exceptions.CatException;
import ru.xplago.catservice.exceptions.NotFoundException;
import ru.xplago.catservice.repositories.CatRepository;
import ru.xplago.catservice.repositories.OwnerRepository;
import ru.xplago.catservice.services.dto.CreateCatDto;
import ru.xplago.common.grpc.validation.validators.ModelValidator;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CatService {
    private CatRepository catRepository;
    private OwnerRepository ownerRepository;
    private OwnerService ownerService;
    private ModelValidator modelValidator;

    public Cat save(Cat cat) {
        return catRepository.save(cat);
    }

    public Cat findCatById(Long id) {
        return catRepository.findById(id).orElseThrow(() -> new NotFoundException("Cat with id " + id + " not found"));
    }

    public Cat findCatByIdAndOwner(Long id, Owner owner) {
        return catRepository.findCatByIdAndOwner(id, owner).orElseThrow(() -> new NotFoundException("Cat with id " + id + " not found for owner"));
    }

    public Cat createCat(CreateCatDto dto) {
        modelValidator.validate(dto);

        Cat cat = Cat.builder()
                .name(dto.getName())
                .birthday(dto.getBirthday())
                .owner(dto.getOwner())
                .tenant(dto.getTenant())
                .avatarId(dto.getAvatarId())
                .created(Timestamp.from(Instant.now()))
                .modified(Timestamp.from(Instant.now()))
                .build();

        return catRepository.save(cat);
    }

    public List<Long> findCatsIdsByOwner(Owner owner) {
        return catRepository.findCatsIdsByOwner(owner);
    }

    public List<Long> findRentedCatsIdsByTenant(Owner tenant) {
        return catRepository.findRentedCatsIdsByTenant(tenant);
    }

    public Cat rebindCat(Long catId, Long oldOwnerId, Long newOwnerId) {
        Cat cat = findCatById(catId);
        Owner oldOwner = ownerService.findById(oldOwnerId);

        if (!cat.getOwner().getId().equals(oldOwner.getId())) {
            throw new CatException("Cat does not belongs to the old owner");
        }
        Owner newOwner = ownerService.findById(newOwnerId);

        cat.setOwner(newOwner);
        return save(cat);
    }
}

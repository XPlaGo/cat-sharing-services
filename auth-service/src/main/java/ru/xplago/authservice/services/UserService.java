package ru.xplago.authservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.authservice.entities.User;
import ru.xplago.authservice.exceptions.UserAlreadyExistsException;
import ru.xplago.authservice.exceptions.UserNotFoundException;
import ru.xplago.authservice.repositories.UserRepository;
import ru.xplago.authservice.services.dto.UserCreateDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class UserService {

    private CryptoService cryptoService;
    private UserRepository userRepository;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", id)));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User create(UserCreateDto userCreateDto) {
        if (existsByEmail(userCreateDto.getEmail()))
            throw new UserAlreadyExistsException("User already exists");

        String salt = cryptoService.generateSalt();
        String hash = cryptoService.encode(userCreateDto.getPassword(), salt);

        return save(User.builder()
                .email(userCreateDto.getEmail())
                .name(userCreateDto.getName())
                .birthday(userCreateDto.getBirthday())
                .password(hash)
                .salt(salt)
                .roles(userCreateDto.getRoles())
                .avatar(userCreateDto.getAvatar())
                .isBlocked(userCreateDto.isBlocked())
                .created(Timestamp.from(Instant.now()))
                .modified(Timestamp.from(Instant.now()))
                .build());
    }
}

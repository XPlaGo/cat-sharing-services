package ru.xplago.authservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.xplago.authservice.entities.User;
import ru.xplago.authservice.entities.UserRole;
import ru.xplago.authservice.exceptions.InvalidPasswordException;
import ru.xplago.authservice.exceptions.UserNotFoundException;
import ru.xplago.authservice.services.dto.*;
import ru.xplago.authservice.topics.UserTopics;
import ru.xplago.common.grpc.security.services.JwtService;
import ru.xplago.common.grpc.security.services.dto.JwtData;
import ru.xplago.common.grpc.validation.validators.ModelValidator;
import ru.xplago.common.kafka.models.user.UserAction;
import ru.xplago.common.kafka.models.user.UserActionModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AuthService {
    private UserService userService;
    private JwtService jwtService;
    private CryptoService cryptoService;
    private ModelValidator modelValidator;
    private KafkaTemplate<Long, UserActionModel> userActionKafkaTemplate;

    public TokenWithUserDto signIn(SignInDto signInDto) {

        modelValidator.validate(signInDto);

        User user = userService.findByEmail(signInDto.getEmail());

        if (user == null) throw new UserNotFoundException("User not found");

        if (!cryptoService.verify(signInDto.getPassword(), user.getSalt(), user.getPassword())) {
            Map<String, Map<String, String>> errors = new HashMap<>();
            Map<String, String> passwordError = new HashMap<>();
            passwordError.put("message", "Invalid password");
            errors.put("password", passwordError);
            throw new InvalidPasswordException("Unsuccessful sign in attempt", errors);
        }

        String accessToken = jwtService.generate(new JwtData(
                user.getId().toString(),
                user.getRoles().stream().map(Enum::toString).collect(Collectors.toSet())
        ));

        return new TokenWithUserDto(accessToken, UserInfoDto.fromUser(user));
    }

    public TokenWithUserDto signUp(SignUpDto signUpDto) {

        modelValidator.validate(signUpDto);

        List<UserRole> userRoles = List.of(UserRole.ROLE_USER);

        User user = userService.create(new UserCreateDto(
                signUpDto.getEmail(),
                signUpDto.getName(),
                signUpDto.getPassword(),
                signUpDto.getBirthday(),
                signUpDto.getAvatar(),
                userRoles,
                false
        ));

        userActionKafkaTemplate.send(
                UserTopics.USER_ACTION,
                user.getId(),
                UserActionModel.builder()
                        .userId(user.getId())
                        .action(UserAction.USER_CREATED)
                        .build()
        );

        String accessToken = jwtService.generate(new JwtData(
                user.getId().toString(),
                user.getRoles().stream().map(Enum::toString).collect(Collectors.toSet())
        ));

        return new TokenWithUserDto(accessToken, UserInfoDto.fromUser(user));
    }

    public TokenWithUserDto refresh(Long userId) {
        User user = userService.findById(userId);

        String accessToken = jwtService.generate(new JwtData(
                user.getId().toString(),
                user.getRoles().stream().map(Enum::toString).collect(Collectors.toSet())
        ));

        return new TokenWithUserDto(accessToken, UserInfoDto.fromUser(user));
    }

    public void deleteUser(Long userId) {
        User user = userService.findById(userId);
        userService.delete(user);
    }
}

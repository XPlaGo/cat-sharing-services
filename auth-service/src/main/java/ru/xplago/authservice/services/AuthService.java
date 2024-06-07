package ru.xplago.authservice.services;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.authservice.entities.User;
import ru.xplago.authservice.entities.UserRole;
import ru.xplago.authservice.exceptions.InvalidPasswordException;
import ru.xplago.authservice.exceptions.UserNotFoundException;
import ru.xplago.authservice.services.dto.*;
import ru.xplago.common.grpc.security.services.JwtService;
import ru.xplago.common.grpc.security.services.dto.JwtData;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AuthService {
    private UserService userService;
    private JwtService jwtService;
    private CryptoService cryptoService;

    public TokenWithUserDto signIn(SignInDto signInDto) {
        User user = userService.findByEmail(signInDto.getEmail());

        if (user == null) throw new UserNotFoundException("User not found");

        if (!cryptoService.verify(signInDto.getPassword(), user.getSalt(), user.getPassword()))
            throw new InvalidPasswordException("Invalid password");

        String accessToken = jwtService.generate(new JwtData(
                user.getEmail(),
                user.getRoles().stream().map(Enum::toString).collect(Collectors.toSet())
        ));

        return new TokenWithUserDto(accessToken, UserInfoDto.fromUser(user));
    }

    public TokenWithUserDto signUp(SignUpDto signUpDto) {

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

        String accessToken = jwtService.generate(new JwtData(
                user.getEmail(),
                user.getRoles().stream().map(Enum::toString).collect(Collectors.toSet())
        ));

        return new TokenWithUserDto(accessToken, UserInfoDto.fromUser(user));
    }

    public TokenWithUserDto refresh(String email) {
        User user = userService.findByEmail(email);

        String accessToken = jwtService.generate(new JwtData(
                user.getEmail(),
                user.getRoles().stream().map(Enum::toString).collect(Collectors.toSet())
        ));

        return new TokenWithUserDto(accessToken, UserInfoDto.fromUser(user));
    }
}

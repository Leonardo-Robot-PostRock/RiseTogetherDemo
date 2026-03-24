package com.ITJobsBackend.authentication.application.usecases.login;

import com.ITJobsBackend.authentication.application.ports.in.LoginPort;
import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.model.User;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginUseCase implements LoginPort {
    private static final Logger log = LoggerFactory.getLogger(LoginUseCase.class);

    private final LoadUserPort loadUserPort;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    public LoginUseCase(
        LoadUserPort loadUserPort,
        PasswordEncoderPort passwordEncoder,
        TokenGeneratorPort tokenGenerator
    ) {
        this.loadUserPort = loadUserPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public AuthTokenResponse execute(LoginCommand command) {
        log.info("Login attempt for email: {}", command.email());

        Email email = Email.of(command.email());

        User user = loadUserPort.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.getPassword().value())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = tokenGenerator.generateAccessToken(
            user.getId().value().toString(),
            user.getRoles()
        );
        String refreshToken = tokenGenerator.generateRefreshToken(
            user.getId().value().toString()
        );

        log.info("User logged in successfully: {}", user.getId());

        return new AuthTokenResponse(
            user.getId().value().toString(),
            user.getUsername().value(),
            user.getEmail().value(),
            accessToken,
            refreshToken
        );
    }
}

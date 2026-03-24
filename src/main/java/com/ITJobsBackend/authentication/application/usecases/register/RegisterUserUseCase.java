package com.ITJobsBackend.authentication.application.usecases.register;

import com.ITJobsBackend.authentication.application.ports.in.RegisterUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.domain.model.User;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyExistsException;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserUseCase implements RegisterUserPort {
    private static final Logger log = LoggerFactory.getLogger(RegisterUserUseCase.class);

    private final SaveUserPort saveUserPort;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUserUseCase(
        SaveUserPort saveUserPort,
        PasswordEncoderPort passwordEncoder
    ) {
        this.saveUserPort = saveUserPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterUserResponse execute(RegisterUserCommand command) {
        log.info("Registering new user with email: {}", command.email());

        // 1. Validar VOs (lanza ValidationException si inválido)
        Email email = Email.of(command.email());
        Username username = Username.of(command.username());
        Password rawPassword = Password.of(command.password());

        // 2. Verificar unicidad (regla de negocio)
        if (saveUserPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email.value());
        }

        // 3. Hash de password (puerto de infraestructura)
        String hashedValue = passwordEncoder.encode(rawPassword.value());
        HashedPassword hashedPassword = HashedPassword.fromHash(hashedValue);

        // 4. Crear agregado (factory method del dominio)
        User user = User.create(username, email, hashedPassword);

        // 5. Persistir (puerto de salida)
        User savedUser = saveUserPort.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getId());

        // 6. Retornar DTO de aplicación
        return new RegisterUserResponse(
            savedUser.getId().value().toString(),
            savedUser.getUsername().value(),
            savedUser.getEmail().value(),
            savedUser.getCreatedAt().value()
        );
    }
}

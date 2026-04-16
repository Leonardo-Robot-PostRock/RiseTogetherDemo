content = open('itjobs-docs/diagrams/class-diagram.puml').read()

old = (
    "RegisterUserPort <.. RegisterUserUseCase\n"
    "LoginPort <.. LoginUseCase\n"
    "ForgotPasswordPort <.. ForgotPasswordUseCase\n"
    "GoogleAuthPort <.. GoogleAuthUseCase\n"
    "VerifyEmailPort <.. VerifyEmailUseCase\n"
    "\n"
    "' Dependencia (<--)\n"
    "SaveUserPort <-- RegisterUserUseCase\n"
    "SaveUserPort <-- ForgotPasswordUseCase\n"
    "SaveUserPort <-- VerifyEmailUseCase\n"
    "SaveUserPort <-- GoogleAuthUseCase\n"
    "LoadUserPort <-- LoginUseCase\n"
    "LoadUserPort <-- ForgotPasswordUseCase\n"
    "LoadUserPort <-- VerifyEmailUseCase\n"
    "LoadUserPort <-- GoogleAuthUseCase\n"
    "PasswordEncoderPort <-- RegisterUserUseCase\n"
    "PasswordEncoderPort <-- LoginUseCase\n"
    "PasswordEncoderPort <-- ForgotPasswordUseCase\n"
    "TokenGeneratorPort <-- LoginUseCase\n"
    "TokenGeneratorPort <-- GoogleAuthUseCase\n"
    "\n"
    "CredentialsVerifier --> PasswordEncoderPort\n"
    "\n"
    "DomainEventPublisher <-- RegisterUserUseCase\n"
    "DomainEventPublisher <-- ForgotPasswordUseCase\n"
    "DomainEventPublisher <-- VerifyEmailUseCase"
)

new = (
    "RegisterUserPort <.. RegisterUserUseCase\n"
    "LoginPort <.. LoginUseCase\n"
    "ChangePasswordPort <.. ChangePasswordUseCase\n"
    "ForgotPasswordPort <.. ForgotPasswordUseCase\n"
    "GoogleAuthPort <.. GoogleAuthUseCase\n"
    "VerifyEmailPort <.. VerifyEmailUseCase\n"
    "\n"
    "' Dependencia (<--)\n"
    "SaveUserPort <-- RegisterUserUseCase\n"
    "SaveUserPort <-- VerifyEmailUseCase\n"
    "SaveUserPort <-- GoogleAuthUseCase\n"
    "SaveUserPort <-- ChangePasswordUseCase\n"
    "LoadUserPort <-- LoginUseCase\n"
    "LoadUserPort <-- ForgotPasswordUseCase\n"
    "LoadUserPort <-- VerifyEmailUseCase\n"
    "LoadUserPort <-- GoogleAuthUseCase\n"
    "LoadUserPort <-- ChangePasswordUseCase\n"
    "PasswordEncoderPort <-- RegisterUserUseCase\n"
    "PasswordEncoderPort <-- ChangePasswordUseCase\n"
    "TokenGeneratorPort <-- LoginUseCase\n"
    "TokenGeneratorPort <-- GoogleAuthUseCase\n"
    "\n"
    "CredentialsVerifier --> PasswordEncoderPort\n"
    "ChangePasswordUseCase --> CredentialsVerifier\n"
    "LoginUseCase --> CredentialsVerifier\n"
    "\n"
    "DomainEventPublisher <-- RegisterUserUseCase\n"
    "DomainEventPublisher <-- ForgotPasswordUseCase\n"
    "DomainEventPublisher <-- VerifyEmailUseCase"
)

if old in content:
    content = content.replace(old, new)
    open('itjobs-docs/diagrams/class-diagram.puml', 'w').write(content)
    print("Done")
else:
    print("NOT FOUND")


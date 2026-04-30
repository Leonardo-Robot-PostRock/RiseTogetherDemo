# ITJobs Backend — Use Case Testing

> Patrón para tests de Use Cases con Mockito BDD (Given / When / Then).

## Estructura

```java
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    // Mocks — solo puertos de salida
    @Mock private QueryUserPort      queryUserPort;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private TokenGeneratorPort  tokenGenerator;

    // Domain service — instancia real
    @Spy private CredentialsVerifier credentialsVerifier;

    // Subject under test
    @InjectMocks private LoginUseCase loginUseCase;

    // Helpers
    private UserView buildUserView() {
        return new UserView(
            UserId.of(UUID.fromString(USER_ID)),
            "johndoe", EMAIL, HashedPassword.fromHash(HASHED_PASSWORD),
            true, true, List.of("ROLE_USER"));
    }

    // Tests
    @Test
    void shouldLoginSuccessfully() {
        // Given
        given(queryUserPort.findByEmail(any(Email.class)))
            .willReturn(Optional.of(buildUserView()));
        given(passwordEncoder.matches("pass123", HASHED_PASSWORD))
            .willReturn(true);
        given(tokenGenerator.generateAccessToken(any(), any()))
            .willReturn("access");
        given(tokenGenerator.generateRefreshToken(any()))
            .willReturn("refresh");

        // When
        AuthTokenResponse response = loginUseCase.execute(
            new LoginCommand(EMAIL, "pass123"));

        // Then
        assertNotNull(response.accessToken());
        then(queryUserPort).should().findByEmail(any(Email.class));
    }
}
```

## Reglas

- Usar **solo** BDDMockito (`given().willReturn()`, `then().should()`)
- No mezclar con Mockito clásico (`when().thenReturn()`)
- Usar `@Spy` para domain services (instancia real)
- Usar `@Mock` solo para **puertos de salida**
- Mocks van en puerto específico según CQRS

## CQRS — Puerto Correcto por Use Case

| Use Case | Puerto | Tipo retornado |
|----------|--------|----------------|
| `LoginUseCase` | `QueryUserPort` | `Optional<UserView>` |
| `RefreshTokenUseCase` | `QueryUserPort` | `Optional<UserView>` |
| `RegisterUserUseCase` | `QueryUserPort` | `boolean` (existsByEmail) |
| `VerifyEmailUseCase` | `LoadUserPort` | `Optional<UserAggregate>` |
| `ChangePasswordUseCase` | `LoadUserPort` | `Optional<UserAggregate>` |

## ArgumentCaptor

```java
@Test
void shouldSaveUserWithCorrectData() {
    // Given
    given(loadUserPort.findByEmail(any(Email.class)))
        .willReturn(Optional.empty());
    given(saveUserPort.save(any(UserAggregate.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // When
    useCase.execute(command);

    // Then
    ArgumentCaptor<UserAggregate> captor = 
        ArgumentCaptor.forClass(UserAggregate.class);
    then(saveUserPort).should().save(captor.capture());
    assertEquals("john", captor.getValue().getUsername().value());
}
```

## Imports

```java
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
```

## Errores a Evitar

- ❌ No mockear agregados o value objects
- ❌ No instanciar subject manualmente
- ❌ No usar `QueryUserPort` en use cases de escritura

## Referencia

- `itjobs-docs/shared/testing.md` — secciones 5, 7, 11
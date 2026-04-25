package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.repository.UserReaderRepository;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaUserRepository;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers.UserMapper;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@ExtendWith(MockitoExtension.class)
class LoadUserPortAdapterTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD_HASH = "$2a$10$hashedpassword";

    @Mock private UserReaderRepository userRepository;
    @Mock private SpringDataJpaUserRepository jpaRepository;
    @Mock private UserMapper mapper;
    @InjectMocks private LoadUserPortAdapter adapter;

    private UserAggregate userAggregate;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userAggregate = UserAggregate.create(
            Username.of("johndoe"), Email.of(EMAIL), HashedPassword.fromHash(PASSWORD_HASH));
        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setEmail(EMAIL);
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        when(jpaRepository.existsByEmail(EMAIL)).thenReturn(true);
        assertTrue(adapter.existsByEmail(Email.of(EMAIL)));
        verify(jpaRepository).existsByEmail(EMAIL);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        when(jpaRepository.existsByEmail(EMAIL)).thenReturn(false);
        assertFalse(adapter.existsByEmail(Email.of(EMAIL)));
        verify(jpaRepository).existsByEmail(EMAIL);
    }

    @Test
    void shouldFindUnverifiedUsersOlderThan() {
        Instant cutoff = Instant.now().minusSeconds(86400);
        when(jpaRepository.findUnverifiedUsersOlderThan(cutoff)).thenReturn(List.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(userAggregate);

        List<UserAggregate> result = adapter.findUnverifiedUsersOlderThan(cutoff);

        assertEquals(1, result.size());
        assertEquals(userAggregate, result.get(0));
        verify(jpaRepository).findUnverifiedUsersOlderThan(cutoff);
    }

    @Test
    void shouldReturnEmptyListWhenNoUnverifiedUsersFound() {
        Instant cutoff = Instant.now();
        when(jpaRepository.findUnverifiedUsersOlderThan(cutoff)).thenReturn(List.of());
        assertTrue(adapter.findUnverifiedUsersOlderThan(cutoff).isEmpty());
        verify(jpaRepository).findUnverifiedUsersOlderThan(cutoff);
    }
}


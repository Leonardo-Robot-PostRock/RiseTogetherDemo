package com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.repository.UserRepository;
import com.risetogether.authentication.domain.valueobjects.Username;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.mappers.write.UserMapper;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {
  private final SpringDataJpaUserRepository jpaRepository;
  private final UserMapper mapper;

  public JpaUserRepositoryAdapter(SpringDataJpaUserRepository jpaRepository, UserMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public UserAggregate save(UserAggregate user) {
    var entity = mapper.toEntity(user);

    @SuppressWarnings("null")
    var savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<UserAggregate> findById(UserId id) {
    return jpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<UserAggregate> findByEmail(Email email) {
    return jpaRepository.findByEmail(email.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<UserAggregate> findByUsername(Username username) {
    return jpaRepository.findByUsername(username.value()).map(mapper::toDomain);
  }
}

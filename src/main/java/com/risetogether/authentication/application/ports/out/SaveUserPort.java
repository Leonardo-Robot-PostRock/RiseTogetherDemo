package com.risetogether.authentication.application.ports.out;

import com.risetogether.authentication.domain.aggregate.UserAggregate;

public interface SaveUserPort {
  UserAggregate save(UserAggregate user);
}

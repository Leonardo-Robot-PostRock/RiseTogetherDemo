package com.ITJobsBackend.authentication.application.usecases.deleteexpiredunverifiedusers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.DeleteUserPort;
import com.ITJobsBackend.authentication.application.ports.out.QueryUserPort;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class DeleteExpiredUnverifiedUsersUseCaseTest {

  private static final UserId USER_ID_1 = UserId.of("550e8400-e29b-41d4-a716-446655440001");
  private static final UserId USER_ID_2 = UserId.of("550e8400-e29b-41d4-a716-446655440002");

  @Mock private QueryUserPort queryUserPort;
  @Mock private DeleteUserPort deleteUserPort;
  @InjectMocks private DeleteExpiredUnverifiedUsersUseCase useCase;

  @Test
  void shouldDeleteUnverifiedUsersOlderThanTwoDays() {
    given(queryUserPort.findUnverifiedUserIdsBefore(any()))
        .willReturn(List.of(USER_ID_1, USER_ID_2));

    assertDoesNotThrow(() -> useCase.execute());

    then(deleteUserPort).should(times(1)).delete(USER_ID_1);
    then(deleteUserPort).should(times(1)).delete(USER_ID_2);
  }

  @Test
  void shouldNotCallDeleteWhenNoUsersToDelete() {
    given(queryUserPort.findUnverifiedUserIdsBefore(any())).willReturn(List.of());

    assertDoesNotThrow(() -> useCase.execute());

    then(deleteUserPort).should(never()).delete(USER_ID_1);
    then(deleteUserPort).should(never()).delete(USER_ID_2);
  }
}

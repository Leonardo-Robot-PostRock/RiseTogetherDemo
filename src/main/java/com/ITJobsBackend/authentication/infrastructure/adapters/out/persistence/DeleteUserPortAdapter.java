package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;
import org.springframework.stereotype.Component;
import com.ITJobsBackend.authentication.application.ports.out.DeleteUserPort;
import com.ITJobsBackend.authentication.domain.repository.UserWriterRepository;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;
@Component
public class DeleteUserPortAdapter implements DeleteUserPort {
  private final UserWriterRepository userRepository;
  public DeleteUserPortAdapter(UserWriterRepository userRepository) {
    this.userRepository = userRepository;
  }
  @Override
  public void delete(UserId id) {
    userRepository.delete(id);
  }
}

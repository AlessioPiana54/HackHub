package hackhub.app.Infrastructure.Repositories;

import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Core.POJO_Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
  extends JpaRepository<User, String>, IUserRepository {
  
  java.util.List<User> findByRuolo(hackhub.app.Core.Enums.Ruolo ruolo);
}

package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Core.POJO_Entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>, IUserRepository {
}

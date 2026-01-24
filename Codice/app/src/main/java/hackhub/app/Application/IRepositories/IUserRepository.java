package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.User;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}

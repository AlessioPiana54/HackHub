package hackhub.app.Application.IRepositories;

import java.util.List;
import java.util.Optional;

import hackhub.app.Core.POJO_Entities.User;

public interface IUserRepository {
    User findByEmail(String email);

    User save(User user);

    Optional<User> findById(String id);

    List<User> findAllById(Iterable<String> ids);
}

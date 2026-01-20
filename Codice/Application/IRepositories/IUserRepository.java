package Application.IRepositories;

import java.util.List;

import Core.POJO_Entities.User;

public interface IUserRepository {
    void save(User user);

    void edit(User user);

    User findById(String id);

    User findByEmail(String email);

    List<User> findAll();

    void deleteById(String id);
}

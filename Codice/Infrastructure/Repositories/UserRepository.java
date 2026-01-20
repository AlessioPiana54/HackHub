package Infrastructure.Repositories;

import Infrastructure.Databases.InMemoryDatabase;

import java.util.List;

import Application.IRepositories.IUserRepository;
import Core.POJO_Entities.User;

public class UserRepository implements IUserRepository {
    private InMemoryDatabase db;

    public UserRepository(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(User user) {
        db.saveUser(user);
    }

    @Override
    public User findById(String id) {
        return db.getUser(id);
    }

    @Override
    public User findByEmail(String email) {
        return db.getUserByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return db.getAllUsers();
    }
}

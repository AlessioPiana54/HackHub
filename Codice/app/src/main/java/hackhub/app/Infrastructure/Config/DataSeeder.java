package hackhub.app.Infrastructure.Config;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Utils.IPasswordHasher;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.POJO_Entities.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {

    private final IUnitOfWork unitOfWork;
    private final IPasswordHasher passwordHasher;

    public DataSeeder(IUnitOfWork unitOfWork, IPasswordHasher passwordHasher) {
        this.unitOfWork = unitOfWork;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (unitOfWork.userRepository().count() > 0) {
            return;
        }

        String defaultPassword = "Test1234!";
        String hashedPassword = passwordHasher.hash(defaultPassword);

        List<User> users = Arrays.asList(
                new User("Mario", "Rossi", "mario@hackhub.it", hashedPassword, Ruolo.ORGANIZZATORE),
                new User("Luigi", "Verdi", "luigi@hackhub.it", hashedPassword, Ruolo.ORGANIZZATORE),
                new User("Giovanni", "Bianchi", "giudice@hackhub.it", hashedPassword, Ruolo.GIUDICE),
                new User("Paolo", "Gialli", "mentore@hackhub.it", hashedPassword, Ruolo.MENTORE),
                new User("Francesca", "Viola", "utente1@hackhub.it", hashedPassword, Ruolo.UTENTE_SENZA_TEAM),
                new User("Matteo", "Rosso", "utente2@hackhub.it", hashedPassword, Ruolo.UTENTE_SENZA_TEAM)
        );

        unitOfWork.userRepository().saveAll(users);
    }
}


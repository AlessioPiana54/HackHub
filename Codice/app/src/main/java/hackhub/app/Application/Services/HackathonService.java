package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Core.Builders.HackathonBuilder;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.User;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class HackathonService {

    private final IHackathonRepository hackathonRepository;
    private final IUserRepository userRepository;

    @Autowired
    public HackathonService(IHackathonRepository hackathonRepo, IUserRepository userRepo) {
        this.hackathonRepository = hackathonRepo;
        this.userRepository = userRepo;
    }

    public Hackathon creaHackathon(CreaHackathonRequest request) {
        User organizzatore = userRepository.findById(request.getIdOrganizzatore())
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato nel database."));
        User giudice = userRepository.findById(request.getIdGiudice())
                .orElseThrow(() -> new IllegalArgumentException("Giudice non trovato nel database."));

        if (organizzatore.getRuolo() != Ruolo.ORGANIZZATORE) {
            throw new SecurityException("L'utente specificato come organizzatore non ha i permessi necessari.");
        }
        if (giudice.getRuolo() != Ruolo.GIUDICE) {
            throw new IllegalArgumentException("L'utente specificato come giudice non ha il ruolo di GIUDICE.");
        }

        List<User> listaMentori = userRepository.findAllById(request.getIdMentori());
        if (listaMentori.size() != request.getIdMentori().size()) {
            throw new IllegalArgumentException("Uno o più mentori non trovati.");
        }

        for (User mentore : listaMentori) {
            if (mentore.getRuolo() != Ruolo.MENTORE) {
                throw new IllegalArgumentException("L'utente " + mentore.getNome() + " non ha il ruolo di MENTORE.");
            }
        }

        StatoHackathon statoIniziale;
        if (!request.getInizioIscrizioni().isAfter(LocalDateTime.now())) {
            statoIniziale = StatoHackathon.IN_ISCRIZIONE;
        } else {
            statoIniziale = StatoHackathon.IN_ATTESA;
        }

        Hackathon nuovoHackathon = new HackathonBuilder()
                .setNome(request.getNome())
                .setRegolamento(request.getRegolamento())
                .setPeriodoIscrizione(request.getInizioIscrizioni(), request.getScadenzaIscrizioni())
                .setDurata(request.getDataInizio(), request.getDataFine())
                .setLuogo(request.getLuogo())
                .setPremioInDenaro(request.getPremioInDenaro())
                .setOrganizzatore(organizzatore)
                .setGiudice(giudice)
                .setMentori(listaMentori)
                .setStato(statoIniziale)
                .build();

        hackathonRepository.save(nuovoHackathon);

        return nuovoHackathon;
    }

    public List<Hackathon> visualizzaTutti() {
        return hackathonRepository.findAll();
    }
}

package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Core.Builders.HackathonBuilder;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.User;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class HackathonService {

    private final IUnitOfWork unitOfWork;

    @Autowired
    public HackathonService(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public Hackathon creaHackathon(CreaHackathonRequest request) {
        User organizzatore = unitOfWork.userRepository().findById(request.getIdOrganizzatore())
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato nel database."));
        User giudice = unitOfWork.userRepository().findById(request.getIdGiudice())
                .orElseThrow(() -> new IllegalArgumentException("Giudice non trovato nel database."));

        if (organizzatore.getRuolo() != Ruolo.ORGANIZZATORE) {
            throw new SecurityException("L'utente specificato come organizzatore non ha i permessi necessari.");
        }
        if (giudice.getRuolo() != Ruolo.GIUDICE) {
            throw new IllegalArgumentException("L'utente specificato come giudice non ha il ruolo di GIUDICE.");
        }

        List<User> listaMentori = unitOfWork.userRepository().findAllById(request.getIdMentori());
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

        unitOfWork.hackathonRepository().save(nuovoHackathon);

        return nuovoHackathon;
    }

    public boolean terminaFaseValutazione(String hackathonId, String giudiceId) {
        Hackathon hackathon = unitOfWork.hackathonRepository().findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

        if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
            throw new IllegalStateException("L'Hackathon non è in fase di valutazione");
        }

        if (!hackathon.getGiudice().getId().equals(giudiceId)) {
            throw new SecurityException("Solo il giudice dell'Hackathon può terminare la valutazione");
        }

        List<Sottomissione> sottomissioni = unitOfWork.sottomissioneRepository()
                .findByPartecipazioneHackathonId(hackathonId);

        for (Sottomissione s : sottomissioni) {
            boolean valutata = unitOfWork.valutazioneRepository().existsBySottomissioneId(s.getId());
            if (!valutata) {
                throw new IllegalStateException(
                        "Non tutte le sottomissioni sono state valutate. Impossibile terminare la fase di valutazione.");
            }
        }

        hackathon.setStato(StatoHackathon.IN_PREMIAZIONE);
        unitOfWork.hackathonRepository().save(hackathon);
        return true;
    }
}

package Application.Services;

import Application.IRepositories.IInvitoRepository;
import Application.IRepositories.IPartecipazioneRepository;
import Application.IRepositories.ITeamRepository;
import Application.IRepositories.IUserRepository;
import Application.Requests.CreaInvitoRequest;
import Application.Requests.RispostaInvitoRequest;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Invito;
import Core.POJO_Entities.Partecipazione;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import java.util.List;

public class InvitoService {
    private final IInvitoRepository invitoRepository;
    private final ITeamRepository teamRepository;
    private final IUserRepository userRepository;
    private final IPartecipazioneRepository partecipazioneRepository;

    public InvitoService(IInvitoRepository invitoRepository, ITeamRepository teamRepository, IUserRepository userRepository, IPartecipazioneRepository partecipazioneRepository) {
        this.invitoRepository = invitoRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.partecipazioneRepository = partecipazioneRepository;
    }

    public Invito inviaInvito(CreaInvitoRequest request) {
        Team team = teamRepository.findById(request.getTeamId());
        User destinatario = userRepository.findById(request.getUserDestinatarioId());
        User mittente = userRepository.findById(request.getUserMittenteId());

        if (team == null) throw new IllegalArgumentException("Team non trovato");
        if (destinatario == null) throw new IllegalArgumentException("Destinatario non trovato");
        if (mittente == null) throw new IllegalArgumentException("Mittente non trovato");

        // Verifica permessi mittente
        if (!team.getLeaderSquadra().getId().equals(mittente.getId()) &&
            team.getMembri().stream().noneMatch(m -> m.getId().equals(mittente.getId()))) {
            throw new IllegalArgumentException("Solo i membri del team o il leader possono inviare inviti.");
        }

        // Verifica destinatario non abbia già un team (Opzionale: o controllato nel frontend/altrove, ma meglio qui)
        if (destinatario.getRuolo() != Ruolo.UTENTE_SENZA_TEAM) {
             throw new IllegalArgumentException("L'utente ha già un team o un ruolo incompatibile.");
        }

        Invito invito = new Invito(team, destinatario, mittente);
        team.getInvitiInSospeso().add(invito);
        invitoRepository.save(invito);
        teamRepository.edit(team); // Salva aggiornamento team
        
        return invito;
    }

    /**
     * Gestisce la risposta, aggiorna lo stato e restituisce l'invito aggiornato.
     */
    public void gestisciRisposta(RispostaInvitoRequest request) {
        Invito invito = invitoRepository.findById(request.getInvitoId());
        if (invito == null) throw new IllegalArgumentException("Invito non trovato");

        User user = userRepository.findById(request.getUserId());
        if (user == null) throw new IllegalArgumentException("Utente non trovato");

        if (!invito.getDestinatario().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Questo invito non è per te.");
        }

        if (request.isAccettato()) {
            accettaInvito(invito, user);
        } else {
            rifiutaInvito(invito);
        }
    }

    private void accettaInvito(Invito invito, User user) {
        Team team = invito.getTeam();
        
        // Verifica Hackathon attivi
        List<Partecipazione> partecipazioni = partecipazioneRepository.findByTeamId(team.getId());
        for (Partecipazione p : partecipazioni) {
            Hackathon h = p.getHackathon();
            if (h.getStato() == StatoHackathon.IN_CORSO ||
                h.getStato() == StatoHackathon.IN_VALUTAZIONE ||
                h.getStato() == StatoHackathon.IN_PREMIAZIONE) {
                throw new IllegalStateException("Impossibile entrare nel team: il team è partecipante ad un Hackathon attivo.");
            }
        }

        // Aggiungi membro
        // Creiamo nuovo utente con ruolo aggiornato (Immutabilità)
        User userAggiornato = new User(user.getId(), user.getNome(), user.getCognome(), user.getEmail(), Ruolo.MEMBRO_TEAM);
        team.getMembri().add(userAggiornato);
        
        // Rimuovi invito
        team.getInvitiInSospeso().remove(invito);

        teamRepository.edit(team);
        userRepository.edit(userAggiornato); // Aggiorna ruolo utente
        invitoRepository.delete(invito);
    }

    private void rifiutaInvito(Invito invito) {
        Team team = invito.getTeam();
        team.getInvitiInSospeso().remove(invito);
        teamRepository.edit(team);
        invitoRepository.delete(invito);
    }
}

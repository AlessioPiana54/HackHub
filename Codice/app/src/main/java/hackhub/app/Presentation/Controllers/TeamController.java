package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.MessageResponse;
import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Services.TeamService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.TeamValidator;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione dei team.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController extends AbstractController {

  private final TeamService teamService;
  private final TeamValidator teamValidator;

  public TeamController(
    TeamService teamService,
    TeamValidator teamValidator,
    ISessionManager sessionManager
  ) {
    super(sessionManager);
    this.teamService = teamService;
    this.teamValidator = teamValidator;
  }

  /**
   * Endpoint di test per verificare se il controller è raggiungibile.
   */
  @GetMapping("/test")
  public ResponseEntity<String> testEndpoint() {
    System.out.println("TeamController.testEndpoint() called!");
    return ResponseEntity.ok("TeamController is working!");
  }

  /**
   * Crea un nuovo team.
   *
   * @param token   Il token di autorizzazione del creatore del team.
   * @param request I dati per la creazione del team.
   * @return Il team creato o un errore di validazione.
   */
  @PostMapping("")
  public ResponseEntity<?> creaTeam(
    @RequestHeader("Authorization") String token,
    @RequestBody CreaTeamRequest request
  ) {
    System.out.println("TeamController.creaTeam() called!");
    System.out.println("Raw token: " + token);

    // Remove "Bearer " prefix if present
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    System.out.println("Clean token: " + token);
    System.out.println("Request: " + request.getNomeTeam());

    User user = getAuthenticatedUser(token);
    validateRequest(teamValidator.validateCreation(request));
    Team team = teamService.creaTeam(request, user.getId());
    return ResponseEntity.ok(team);
  }

  /**
   * Aggiorna i dettagli di un team.
   *
   * @param token   Il token di autorizzazione del leader del team.
   * @param teamId  L'ID del team da aggiornare.
   * @param request I nuovi dati del team (nome e/o descrizione).
   * @return Il team aggiornato.
   */
  @PutMapping("/{teamId}")
  public ResponseEntity<?> updateTeam(
    @RequestHeader("Authorization") String token,
    @PathVariable String teamId,
    @RequestBody hackhub.app.Application.Requests.UpdateTeamRequest request
  ) {
    System.out.println("TeamController.updateTeam() called!");
    
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    
    User user = getAuthenticatedUser(token);
    validateIds(teamId);
    
    // Validazione base
    if ((request.getNomeTeam() == null || request.getNomeTeam().trim().isEmpty())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Nessun dato da aggiornare fornito."));
    }
    
    Team team = teamService.updateTeam(teamId, request, user.getId());
    return ResponseEntity.ok(team);
  }

  /**
   * Iscrive un team a un hackathon.
   *
   * @param token       Il token di autorizzazione del leader del team.
   * @param teamId      L'ID del team da iscrivere.
   * @param hackathonId L'ID dell'hackathon a cui iscriversi.
   * @return La partecipazione creata o un errore di validazione.
   */
  @PostMapping("/{teamId}/iscrivi")
  public ResponseEntity<?> iscriviTeam(
    @RequestHeader("Authorization") String token,
    @PathVariable String teamId,
    @RequestParam String hackathonId
  ) {
    User user = getAuthenticatedUser(token);
    validateIds(teamId, hackathonId);
    Partecipazione partecipazione = teamService.iscriviTeam(
      teamId,
      hackathonId,
      user.getId(),
      token
    );
    return ResponseEntity.ok(partecipazione);
  }

  /**
   * Permette a un utente di abbandonare un team.
   *
   * @param token  Il token di autorizzazione dell'utente.
   * @param teamId L'ID del team da abbandonare.
   * @return Un messaggio di conferma.
   */
  @PostMapping("/{teamId}/abbandona")
  public ResponseEntity<?> abbandonaTeam(
    @RequestHeader("Authorization") String token,
    @PathVariable String teamId
  ) {
    System.out.println("TeamController.abbandonaTeam() called!");
    System.out.println("Raw token: " + token);

    // Remove "Bearer " prefix if present
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    System.out.println("Clean token: " + token);
    System.out.println("Team ID: " + teamId);

    User user = getAuthenticatedUser(token);
    validateIds(teamId);
    teamService.abbandonaTeam(teamId, user.getId(), token);
    return ResponseEntity.ok(
      new MessageResponse("Hai abbandonato il team con successo.")
    );
  }

  /**
   * Trasferisce il ruolo di Leader a un altro membro del team.
   *
   * @param token       Il token di autorizzazione dell'attuale leader.
   * @param teamId      L'ID del team.
   * @param newLeaderId L'ID del nuovo leader.
   * @return Il team aggiornato.
   */
  @PatchMapping("/{teamId}/leader/{newLeaderId}")
  public ResponseEntity<?> trasferisciLeadership(
    @RequestHeader("Authorization") String token,
    @PathVariable String teamId,
    @PathVariable String newLeaderId
  ) {
    System.out.println("TeamController.trasferisciLeadership() called!");
    // Remove "Bearer " prefix if present
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    User user = getAuthenticatedUser(token);
    validateIds(teamId, newLeaderId);
    Team team = teamService.trasferisciLeadership(teamId, newLeaderId, user.getId());
    return ResponseEntity.ok(team);
  }

  /**
   * Recupera i team di cui l'utente è membro.
   *
   * @param token Il token di autorizzazione dell'utente.
   * @return Lista di TeamDTO.
   */
  @GetMapping("/my-teams")
  public ResponseEntity<List<TeamDTO>> getMyTeams(
    @RequestHeader("Authorization") String token
  ) {
    System.out.println("TeamController.getMyTeams() called!");
    System.out.println("Raw token: " + token);

    // Remove "Bearer " prefix if present
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    System.out.println("Clean token: " + token);

    User user = getAuthenticatedUser(token);
    List<TeamDTO> teams = teamService.getUserTeams(user.getId());
    System.out.println("Teams found: " + teams.size());
    return ResponseEntity.ok(teams);
  }

  /**
   * Recupera i dettagli di un team specifico.
   *
   * @param teamId L'ID del team.
   * @param token  Il token di autorizzazione.
   * @return TeamDTO con dettagli completi.
   */
  @GetMapping("/{teamId}")
  public ResponseEntity<TeamDTO> getTeamDetails(
    @PathVariable String teamId,
    @RequestHeader("Authorization") String token
  ) {
    System.out.println("TeamController.getTeamDetails() called!");
    System.out.println("Raw token: " + token);

    // Remove "Bearer " prefix if present
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    System.out.println("Clean token: " + token);
    System.out.println("Team ID: " + teamId);

    User user = getAuthenticatedUser(token);
    validateIds(teamId);
    TeamDTO team = teamService.getTeamDetails(teamId);
    return ResponseEntity.ok(team);
  }

  /**
   * **PREVENZIONE: Endpoint per pulire team orfani**
   *
   * @param token Il token di autorizzazione.
   * @return Messaggio di conferma.
   */
  @PostMapping("/cleanup")
  public ResponseEntity<MessageResponse> cleanupOrphanedTeams(
    @RequestHeader("Authorization") String token
  ) {
    System.out.println("TeamController.cleanupOrphanedTeams() called!");

    // Remove "Bearer " prefix if present
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    User user = getAuthenticatedUser(token);

    // Solo admin può eseguire cleanup (opzionale)
    teamService.cleanupOrphanedTeams();

    return ResponseEntity.ok(
      new MessageResponse("Cleanup completato con successo")
    );
  }
}

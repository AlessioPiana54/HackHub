package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.MessageResponse;
import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Services.TeamService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.TeamValidator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione dei team.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController extends AbstractController {

  private static final Logger logger = LoggerFactory.getLogger(TeamController.class);
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
    logger.debug("TeamController.creaTeam() called!");

    User user = getAuthenticatedUser(token);
    validateRequest(teamValidator.validateCreation(request));
    Team team = teamService.creaTeam(request, user.getId());
    logger.info("Team creato con successo: {}", team.getNomeTeam());
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
    logger.debug("TeamController.updateTeam() called for teamId: {}", teamId);
    
    User user = getAuthenticatedUser(token);
    validateIds(teamId);
    
    // Validazione base
    if ((request.getNomeTeam() == null || request.getNomeTeam().trim().isEmpty())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Nessun dato da aggiornare fornito."));
    }
    
    Team team = teamService.updateTeam(teamId, request, user.getId());
    logger.info("Team aggiornato: {}", team.getNomeTeam());
    return ResponseEntity.ok(team);
  }

  /**
   * Permette a un utente di abbandonare un team.
   *
   * @param token  Il token di autorizzazione dell'utente.
   * @param teamId L'ID del team da abbandonare.
   * @return Un messaggio di conferma.
   */
  @DeleteMapping("/{teamId}/members/me")
  public ResponseEntity<?> abbandonaTeam(
    @RequestHeader("Authorization") String token,
    @PathVariable String teamId
  ) {
    logger.debug("TeamController.abbandonaTeam() called for teamId: {}", teamId);

    User user = getAuthenticatedUser(token);
    validateIds(teamId);
    teamService.abbandonaTeam(teamId, user.getId());
    logger.info("Utente {} ha abbandonato il team {}", user.getId(), teamId);
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
    logger.debug("TeamController.trasferisciLeadership() called for teamId: {}, newLeaderId: {}", teamId, newLeaderId);

    User user = getAuthenticatedUser(token);
    validateIds(teamId, newLeaderId);
    Team team = teamService.trasferisciLeadership(teamId, newLeaderId, user.getId());
    logger.info("Leadership trasferita nel team {} a {}", teamId, newLeaderId);
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
    logger.debug("TeamController.getMyTeams() called");

    User user = getAuthenticatedUser(token);
    List<TeamDTO> teams = teamService.getUserTeams(user.getId());
    logger.debug("Teams found: {}", teams.size());
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
    logger.debug("TeamController.getTeamDetails() called for teamId: {}", teamId);

    getAuthenticatedUser(token); // Verifica che l'utente sia autenticato
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
    logger.info("TeamController.cleanupOrphanedTeams() called");

    User user = getAuthenticatedUser(token);
    validateUserRole(user, hackhub.app.Core.Enums.Ruolo.ORGANIZZATORE, "Solo gli organizzatori possono eseguire la pulizia.");

    teamService.cleanupOrphanedTeams();

    return ResponseEntity.ok(
      new MessageResponse("Cleanup completato con successo")
    );
  }
}

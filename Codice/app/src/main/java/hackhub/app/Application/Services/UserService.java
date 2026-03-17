package hackhub.app.Application.Services;

import hackhub.app.Application.DTOs.UserDTO;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.UpdateProfileRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.POJO_Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servizio responsabile della gestione dei profili utente.
 */
@Service
@Transactional
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(
    UserService.class
  );
  private final IUnitOfWork unitOfWork;

  public UserService(IUnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  /**
   * Recupera i dati del profilo utente.
   *
   * @param userId L'ID dell'utente.
   * @return Il DTO con i dati dell'utente.
   */
  public UserDTO getUserProfile(String userId) {
    logger.info("Getting user profile for userId: {}", userId);

    try {
      User user = unitOfWork
        .userRepository()
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

      logger.info("Found user: {} {}", user.getNome(), user.getCognome());

      UserDTO userDTO = new UserDTO(
        user.getId(),
        user.getNome(),
        user.getCognome(),
        user.getEmail(),
        user.getRuolo()
      );

      logger.info("Returning userDTO for userId: {}", userId);
      return userDTO;
    } catch (Exception e) {
      logger.error("Error getting user profile for userId: {}", userId, e);
      throw e;
    }
  }

  /**
   * Aggiorna i dati del profilo utente.
   *
   * @param userId L'ID dell'utente.
   * @param request I dati aggiornati.
   * @return Il DTO con i dati aggiornati.
   */
  public UserDTO updateProfile(String userId, UpdateProfileRequest request) {
    User user = unitOfWork
      .userRepository()
      .findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

    // Aggiorna solo i campi permessi
    if (request.getNome() != null && !request.getNome().trim().isEmpty()) {
      user.setNome(request.getNome());
    }
    if (
      request.getCognome() != null && !request.getCognome().trim().isEmpty()
    ) {
      user.setCognome(request.getCognome());
    }

    User updatedUser = unitOfWork.userRepository().save(user);

    return new UserDTO(
      updatedUser.getId(),
      updatedUser.getNome(),
      updatedUser.getCognome(),
      updatedUser.getEmail(),
      updatedUser.getRuolo()
    );
  }

  /**
   * Recupera tutti gli utenti in base al loro ruolo.
   *
   * @param ruolo Il ruolo cercato.
   * @return La lista dei DTO utente.
   */
  public List<UserDTO> getUsersByRuolo(Ruolo ruolo) {
    return unitOfWork.userRepository().findByRuolo(ruolo).stream()
      .map(user -> new UserDTO(
        user.getId(),
        user.getNome(),
        user.getCognome(),
        user.getEmail(),
        user.getRuolo()
      ))
      .collect(Collectors.toList());
  }
}

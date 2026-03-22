package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.UserDTO;
import hackhub.app.Application.Requests.UpdateProfileRequest;
import hackhub.app.Core.Enums.Ruolo;

import java.util.List;

public interface IUserService {
    UserDTO getUserProfile(String userId);
    UserDTO updateProfile(String userId, UpdateProfileRequest request);
    List<UserDTO> getUsersByRuolo(Ruolo ruolo);
}

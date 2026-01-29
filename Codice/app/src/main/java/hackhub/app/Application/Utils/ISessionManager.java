package hackhub.app.Application.Utils;

import hackhub.app.Core.POJO_Entities.User;

public interface ISessionManager {
    String createSession(User user);

    User getUser(String token);

    void invalidateSession(String token);
}

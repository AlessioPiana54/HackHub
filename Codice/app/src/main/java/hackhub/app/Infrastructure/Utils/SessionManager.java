package hackhub.app.Infrastructure.Utils;

import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SessionManager implements ISessionManager {

  private final Map<String, User> sessions = new ConcurrentHashMap<>();

  @Override
  public String createSession(User user) {
    String token = UUID.randomUUID().toString();
    sessions.put(token, user);
    return token;
  }

  @Override
  public User getUser(String token) {
    if (token == null) return null;
    return sessions.get(token);
  }

  @Override
  public void invalidateSession(String token) {
    if (token != null) {
      sessions.remove(token);
    }
  }
}

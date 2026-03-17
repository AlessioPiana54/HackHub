package hackhub.app.Infrastructure.Repositories;

import hackhub.app.Application.IRepositories.ITeamRepository;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TeamRepository
  extends JpaRepository<Team, String>, ITeamRepository {
  @Override
  @Query(
    "SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Team t WHERE t.leaderSquadra.id = :leaderId"
  )
  boolean existsByLeaderId(@Param("leaderId") String leaderId);

  @Override
  @Modifying
  @Transactional
  @Query("DELETE FROM Team t WHERE t.leaderSquadra.id = :leaderId")
  void deleteByLeaderId(@Param("leaderId") String leaderId);

  @Override
  @Query("SELECT t FROM Team t WHERE t.id = :id")
  Optional<Team> findByIdString(@Param("id") String id);

  @Override
  boolean existsByNomeTeam(String nomeTeam);

  @Override
  java.util.List<Team> findByMembriId(String userId);
}

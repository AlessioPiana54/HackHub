package hackhub.app.Infrastructure.Repositories;

import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonRepository
  extends JpaRepository<Hackathon, String>, IHackathonRepository {

  @Override
  @Query("SELECT h FROM Hackathon h WHERE h.stato NOT IN :stati")
  List<Hackathon> findByStatoNotIn(@Param("stati") Collection<StatoHackathon> stati);

  @Override
  @Query("SELECT h FROM Hackathon h WHERE h.id = :id")
  Optional<Hackathon> findByIdString(@Param("id") String id);

  @Query("SELECT h FROM Hackathon h WHERE h.giudice.id = :giudiceId")
  List<Hackathon> findByGiudiceId(@Param("giudiceId") String giudiceId);

  @Query("SELECT h FROM Hackathon h JOIN h.mentori m WHERE m.id = :mentoreId")
  List<Hackathon> findByMentoriId(@Param("mentoreId") String mentoreId);

  @Override
  Optional<Hackathon> findByNome(String nome);
}

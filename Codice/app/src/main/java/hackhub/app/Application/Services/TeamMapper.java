package hackhub.app.Application.Services;

import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Core.POJO_Entities.Team;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper per la conversione tra Team entity e TeamDTO.
 * Centralizza la logica di mapping per mantenere i service puliti.
 */
@Component
public class TeamMapper {

    /**
     * Converte un'entità Team in un TeamDTO.
     *
     * @param team l'entità Team da convertire
     * @return il TeamDTO corrispondente
     */
    public TeamDTO toDTO(Team team) {
        if (team == null) {
            return null;
        }

        List<String> membriIds = team.getMembri().stream()
                .map(member -> member.getId())
                .collect(Collectors.toList());

        List<String> membriNomi = team.getMembri().stream()
                .map(member -> member.getNome() + " " + member.getCognome())
                .collect(Collectors.toList());

        String leaderNome = team.getLeaderSquadra() != null ? 
                team.getLeaderSquadra().getNome() + " " + team.getLeaderSquadra().getCognome() : 
                null;

        return new TeamDTO(
                team.getId(),
                team.getNomeTeam(),
                team.getLeaderSquadra() != null ? team.getLeaderSquadra().getId() : null,
                leaderNome,
                membriIds,
                membriNomi,
                team.getDataCreazione()
        );
    }
}

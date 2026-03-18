package hackhub.app.Application.Services;

import hackhub.app.Application.DTOs.HackathonSummaryDTO;
import hackhub.app.Core.POJO_Entities.Hackathon;
import org.springframework.stereotype.Component;

/**
 * Mapper per la conversione tra Hackathon entity e HackathonSummaryDTO.
 * Centralizza la logica di mapping per mantenere i service puliti.
 */
@Component
public class HackathonMapper {

    /**
     * Converte un'entità Hackathon in un HackathonSummaryDTO.
     *
     * @param hackathon l'entità Hackathon da convertire
     * @return l'HackathonSummaryDTO corrispondente
     */
    public HackathonSummaryDTO toSummaryDTO(Hackathon hackathon) {
        if (hackathon == null) {
            return null;
        }

        String organizzatoreNome = hackathon.getOrganizzatore() != null ?
                hackathon.getOrganizzatore().getNome() + " " + hackathon.getOrganizzatore().getCognome() :
                null;

        return new HackathonSummaryDTO(
                hackathon.getId(),
                hackathon.getNome(),
                hackathon.getRegolamento(),
                null, // description field doesn't exist in Hackathon entity
                hackathon.getLogoUrl(),
                hackathon.getInizioIscrizioni(),
                hackathon.getScadenzaIscrizioni(),
                hackathon.getDataInizio(),
                hackathon.getDataFine(),
                hackathon.getLuogo(),
                hackathon.getPremioInDenaro(),
                hackathon.getStato(),
                organizzatoreNome
        );
    }
}

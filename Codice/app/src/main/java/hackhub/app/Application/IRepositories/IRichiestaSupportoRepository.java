package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.RichiestaSupporto;

public interface IRichiestaSupportoRepository {
    /**
     * Trova tutte le richieste di supporto per un hackathon specifico.
     * 
     * @param hackathonId ID dell'hackathon.
     * @return Lista di richieste di supporto.
     */
    java.util.List<RichiestaSupporto> findByPartecipazioneHackathonId(String hackathonId);

    /**
     * Trova tutte le richieste di supporto di un team specifico per un hackathon.
     * 
     * @param hackathonId ID dell'hackathon.
     * @param teamId      ID del team.
     * @return Lista di richieste di supporto filtrate.
     */
    java.util.List<RichiestaSupporto> findByPartecipazioneHackathonIdAndPartecipazioneTeamId(String hackathonId,
            String teamId);

    /**
     * Salva o aggiorna una richiesta di supporto.
     * 
     * @param richiestaSupporto L'entità RichiestaSupporto da salvare.
     * @return L'entità salvata.
     */
    RichiestaSupporto save(RichiestaSupporto richiestaSupporto);

    /**
     * Trova una richiesta di supporto tramite ID.
     * 
     * @param id Identificativo univoco della richiesta.
     * @return Optional contenente la richiesta se trovata.
     */
    java.util.Optional<RichiestaSupporto> findById(String id);
}

package hackhub.app.Core.Builders;

import java.time.LocalDateTime;
import java.util.List;

import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Hackathon;

/**
 * Interfaccia per il pattern Builder per la creazione di oggetti Hackathon.
 *
 * @see hackhub.app.Core.POJO_Entities.Hackathon
 */
public interface IHackathonBuilder {
    IHackathonBuilder setNome(String nome);

    IHackathonBuilder setRegolamento(String regolamento);

    IHackathonBuilder setPeriodoIscrizione(LocalDateTime inizio, LocalDateTime scadenza);

    IHackathonBuilder setDurata(LocalDateTime inizio, LocalDateTime fine);

    IHackathonBuilder setLuogo(String luogo);

    IHackathonBuilder setPremioInDenaro(double premioInDenaro);

    IHackathonBuilder setOrganizzatore(User organizzatore);

    IHackathonBuilder setGiudice(User giudice);

    IHackathonBuilder setMentori(List<User> mentori);

    IHackathonBuilder setStato(StatoHackathon stato);

    Hackathon build();
}

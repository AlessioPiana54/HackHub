package hackhub.app;

import hackhub.app.Application.IRepositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RepositoryLayerTest {

    @Autowired
    private IHackathonRepository hackathonRepository;
    @Autowired
    private IInvitoRepository invitoRepository;
    @Autowired
    private IPartecipazioneRepository partecipazioneRepository;
    @Autowired
    private IRichiestaSupportoRepository richiestaSupportoRepository;
    @Autowired
    private ISegnalazioneRepository segnalazioneRepository;
    @Autowired
    private ISottomissioneRepository sottomissioneRepository;
    @Autowired
    private ITeamRepository teamRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IValutazioneRepository valutazioneRepository;

    @Test
    public void contextLoadsAndRepositoriesAreInjectable() {
        assertThat(hackathonRepository).isNotNull();
        assertThat(invitoRepository).isNotNull();
        assertThat(partecipazioneRepository).isNotNull();
        assertThat(richiestaSupportoRepository).isNotNull();
        assertThat(segnalazioneRepository).isNotNull();
        assertThat(sottomissioneRepository).isNotNull();
        assertThat(teamRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(valutazioneRepository).isNotNull();
    }
}

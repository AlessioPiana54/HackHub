package hackhub.app.Core.POJO_Entities;

import hackhub.app.Core.Enums.StatoHackathon;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Rappresenta un evento Hackathon.
 * <p>
 * Contiene i dettagli dell'evento, le regole, le date e i riferimenti agli
 * utenti chiave (Organizzatore, Giudice, Mentori).
 * </p>
 */
@Entity
@Table(name = "hackathons", indexes = {
  @Index(name = "idx_hackathons_stato", columnList = "stato"),
  @Index(name = "idx_hackathons_organizzatore", columnList = "organizzatore_id")
})
public class Hackathon {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String regolamento;

  @Column(nullable = false)
  private LocalDateTime inizioIscrizioni;

  @Column(nullable = false)
  private LocalDateTime scadenzaIscrizioni;

  @Column(nullable = false)
  private LocalDateTime dataInizio;

  @Column(nullable = false)
  private LocalDateTime dataFine;

  @Column(nullable = false)
  private String luogo;

  private String logoUrl;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal premioInDenaro;

  @Column(nullable = false)
  private LocalDateTime dataCreazione;

  @Enumerated(EnumType.STRING)
  private StatoHackathon stato;

  // Riferimenti diretti a User
  @ManyToOne
  @JoinColumn(name = "organizzatore_id")
  private User organizzatore;

  @ManyToOne
  @JoinColumn(name = "giudice_id")
  private User giudice;

  @ManyToOne
  @JoinColumn(name = "vincitore_id")
  private Team vincitore;

  // Liste
  @ManyToMany
  @JoinTable(
    name = "hackathon_mentori",
    joinColumns = @JoinColumn(name = "hackathon_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private List<User> mentori;

  public Hackathon() {}

  public Hackathon(
    String nome,
    String regolamento,
    LocalDateTime inizioIscrizioni,
    LocalDateTime scadenzaIscrizioni,
    LocalDateTime dataInizio,
    LocalDateTime dataFine,
    String luogo,
    BigDecimal premioInDenaro,
    User organizzatore,
    User giudice,
    List<User> mentori,
    StatoHackathon stato
  ) {
    this.nome = nome;
    this.regolamento = regolamento;
    this.inizioIscrizioni = inizioIscrizioni;
    this.scadenzaIscrizioni = scadenzaIscrizioni;
    this.dataInizio = dataInizio;
    this.dataFine = dataFine;
    this.luogo = luogo;
    this.premioInDenaro = premioInDenaro;
    this.organizzatore = organizzatore;
    this.giudice = giudice;
    this.mentori = mentori;
    this.stato = stato;
    this.dataCreazione = LocalDateTime.now();
  }

  public String getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getRegolamento() {
    return regolamento;
  }

  public void setRegolamento(String regolamento) {
    this.regolamento = regolamento;
  }

  public void setPeriodoIscrizione(
    LocalDateTime inizio,
    LocalDateTime scadenza
  ) {
    this.inizioIscrizioni = inizio;
    this.scadenzaIscrizioni = scadenza;
  }

  public void setDurata(LocalDateTime inizio, LocalDateTime fine) {
    this.dataInizio = inizio;
    this.dataFine = fine;
  }

  public void setOrganizzatore(User organizzatore) {
    this.organizzatore = organizzatore;
  }

  public void setGiudice(User giudice) {
    this.giudice = giudice;
  }

  public void setMentori(List<User> mentori) {
    this.mentori = mentori;
  }

  public User getOrganizzatore() {
    return organizzatore;
  }

  public User getGiudice() {
    return giudice;
  }

  public Team getVincitore() {
    return vincitore;
  }

  public void setVincitore(Team vincitore) {
    this.vincitore = vincitore;
  }

  public List<User> getMentori() {
    return mentori;
  }

  public StatoHackathon getStato() {
    return stato;
  }

  public void setStato(StatoHackathon stato) {
    this.stato = stato;
  }

  public LocalDateTime getDataInizio() {
    return dataInizio;
  }

  public LocalDateTime getDataFine() {
    return dataFine;
  }

  public LocalDateTime getInizioIscrizioni() {
    return inizioIscrizioni;
  }

  public LocalDateTime getScadenzaIscrizioni() {
    return scadenzaIscrizioni;
  }

  public String getLuogo() {
    return luogo;
  }

  public void setLuogo(String luogo) {
    this.luogo = luogo;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public BigDecimal getPremioInDenaro() {
    return premioInDenaro;
  }

  public void setPremioInDenaro(BigDecimal premioInDenaro) {
    this.premioInDenaro = premioInDenaro;
  }

  public LocalDateTime getDataCreazione() {
    return dataCreazione;
  }

  // @Version per il controllo di concorrenza
  @Version
  private Long version;
}

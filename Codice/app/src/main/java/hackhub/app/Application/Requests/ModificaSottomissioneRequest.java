package hackhub.app.Application.Requests;

public class ModificaSottomissioneRequest {

  private String linkProgetto;
  private String descrizione;

  public ModificaSottomissioneRequest(String linkProgetto, String descrizione) {
    this.linkProgetto = linkProgetto;
    this.descrizione = descrizione;
  }

  public String getLinkProgetto() {
    return linkProgetto;
  }

  public String getDescrizione() {
    return descrizione;
  }
}

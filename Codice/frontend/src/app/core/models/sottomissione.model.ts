export interface Sottomissione {
  id: string;
  linkProgetto: string;
  descrizione: string;
  dataInvio: string;
  partecipazione: {
    id: string;
    team: {
      id: string;
      nomeTeam: string;
    };
    hackathon: {
      id: string;
      nome: string;
    };
  };
  mittente: {
    id: string;
    nome: string;
    cognome: string;
  };
  valutazioni: Array<Valutazione>;
}

export interface Valutazione {
  id: string;
  giudizio: string;
  voto: number;
  giudice: {
    id: string;
    nome: string;
    cognome: string;
  };
  sottomissione: {
    id: string;
  };
}

export interface InviaSottomissioneRequest {
  idTeam: string;
  idHackathon: string;
  linkProgetto: string;
  descrizione: string;
}

export interface ModificaSottomissioneRequest {
  linkProgetto: string;
  descrizione: string;
}

export interface CreaValutazioneRequest {
  giudizio: string;
  voto: number;
}

export interface SottomissioneDTO {
  id: string;
  linkProgetto: string;
  descrizione: string;
  dataInvio: string;
  partecipazioneId: string;
  nomeTeam: string;
  nomeHackathon: string;
  mittente: {
    id: string;
    nome: string;
    cognome: string;
  };
  valutazioni: ValutazioneDTO[];
}

export interface ValutazioneDTO {
  id: string;
  giudizio: string;
  voto: number;
  giudice: {
    id: string;
    nome: string;
    cognome: string;
  };
}

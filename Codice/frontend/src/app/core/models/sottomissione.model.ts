/**
 * Sottomissione restituita dal backend (SottomissioneDTO).
 * voto e giudizio sono null finché il giudice non la valuta.
 */
export interface Sottomissione {
  id: string;
  hackathonId: string;
  hackathonNome: string;
  teamId: string;
  teamNome: string;
  mittenteId: string;
  mittenteNome: string;
  linkProgetto: string;
  descrizione: string;
  dataSottomissione: string;
  voto: number | null;
  giudizio: string | null;
}

/** Valutazione restituita dal backend (ValutazioneDTO). */
export interface Valutazione {
  id: string;
  sottomissioneId: string;
  teamId: string;
  teamNome: string;
  giudiceId: string;
  giudiceNome: string;
  voto: number;
  giudizio: string;
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

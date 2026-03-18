export enum StatoHackathon {
  IN_ATTESA = 'IN_ATTESA',
  IN_ISCRIZIONE = 'IN_ISCRIZIONE',
  IN_CORSO = 'IN_CORSO',
  IN_VALUTAZIONE = 'IN_VALUTAZIONE',
  IN_PREMIAZIONE = 'IN_PREMIAZIONE',
  CONCLUSO = 'CONCLUSO'
}

export interface HackathonSummaryDTO {
  id: string;
  nome: string;
  regolamento: string;
  descrizione?: string;
  logoUrl?: string;
  inizioIscrizioni: string;
  scadenzaIscrizioni: string;
  dataInizio: string;
  dataFine: string;
  luogo: string;
  premioInDenaro: number;
  stato: StatoHackathon;
  organizzatoreNome: string;
}

export interface CreaHackathonRequest {
  nome: string;
  regolamento: string;
  inizioIscrizioni: string;
  scadenzaIscrizioni: string;
  dataInizio: string;
  dataFine: string;
  luogo: string;
  logoUrl?: string;
  premioInDenaro: number;
  idGiudice: string;
  idMentori: string[];
}

export interface Hackathon {
  id: string;
  nome: string;
  regolamento: string;
  dataInizio: string;
  dataFine: string;
  luogo: string;
  premioInDenaro: number;
  stato: StatoHackathon;
  organizzatore: {
    id: string;
    nome: string;
    cognome: string;
  };
  mentori: Array<{
    id: string;
    nome: string;
    cognome: string;
  }>;
  giudice: {
    id: string;
    nome: string;
    cognome: string;
  };
}

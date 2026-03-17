export interface InvitoDTO {
  id: string;
  mittenteId: string;
  mittenteNome: string;
  destinatarioId: string;
  destinatarioNome: string;
  teamId: string;
  teamNome: string;
  dataInvio: string;
}

export interface CreaInvitoRequest {
  teamId: string;
  emailDestinatario: string;
}

export interface RispostaInvitoRequest {
  accettato: boolean;
}

export interface Invito {
  id: string;
  team: {
    id: string;
    nomeTeam: string;
  };
  destinatario: {
    id: string;
    nome: string;
    cognome: string;
    email: string;
  };
  mittente: {
    id: string;
    nome: string;
    cognome: string;
    email: string;
  };
  dataInvito: string;
}

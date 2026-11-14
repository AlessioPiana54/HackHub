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

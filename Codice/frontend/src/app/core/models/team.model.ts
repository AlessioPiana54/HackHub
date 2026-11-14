export interface TeamDTO {
  id: string;
  nome: string;
  leaderId: string;
  leaderNome: string;
  membriIds: string[];
  membriNomi: string[];
  dataCreazione: string | null;
}

export interface CreaTeamRequest {
  nomeTeam: string;
}

export interface UpdateTeamRequest {
  nomeTeam?: string;
}

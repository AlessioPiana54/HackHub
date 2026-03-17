export enum Ruolo {
  UTENTE_SENZA_TEAM = 'UTENTE_SENZA_TEAM',
  LEADER_TEAM = 'LEADER_TEAM',
  MEMBRO_TEAM = 'MEMBRO_TEAM',
  ORGANIZZATORE = 'ORGANIZZATORE',
  GIUDICE = 'GIUDICE',
  MENTORE = 'MENTORE'
}

export interface UserDTO {
  id: string;
  nome: string;
  cognome: string;
  email: string;
  ruolo: string; // Cambiato da Ruolo a string per compatibilità
}

// Alias per compatibilità con codice esistente
export type User = UserDTO;

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user?: UserDTO;
}

export interface RegisterRequest {
  nome: string;
  cognome: string;
  email: string;
  password: string;
}

export interface UpdateProfileRequest {
  nome: string;
  cognome: string;
}

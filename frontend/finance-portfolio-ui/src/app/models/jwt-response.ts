export interface JwtResponse {
  accessToken: string;
  tokenType: string;
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface MessageResponse {
  message: string;
}

export interface LoginRequestDto {
  email: string;
  password: string;
}

export interface RegisterRequestDto {
  name: string;
  email: string;
  password: string;
  phone?: string;
  document?: string;
}

export interface AuthSessionDto {
  accessToken: string;
  refreshToken?: string;
  expiresIn?: number;
  tokenType?: "Bearer";
  user?: AuthUserDto;
}

export interface AuthUserDto {
  id: string;
  name: string;
  email: string;
  roles?: string[];
}

export interface PasswordResetRequestDto {
  email: string;
}


export interface UserDto {
  id: string;
  name: string;
  email: string;
  phone?: string;
  document?: string;
  avatarUrl?: string;
  createdAt?: string;
}

export interface UpdateUserRequestDto {
  name?: string;
  phone?: string;
  avatarUrl?: string;
}

export interface UpdatePasswordRequestDto {
  currentPassword: string;
  newPassword: string;
}


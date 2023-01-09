package com.shuttle.user.passwordReset.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordResetCodeDTO {
	private String new_password;
	private String code;
}

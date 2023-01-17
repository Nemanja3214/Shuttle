package com.shuttle.user.passwordReset.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordResetCodeDTO {
	private String newPassword;
	private String code;
}

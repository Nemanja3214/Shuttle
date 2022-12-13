package com.shuttle.admin;

import com.shuttle.common.Entity;
import com.shuttle.credentials.dto.Credentials;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@jakarta.persistence.Entity
@Data
@NoArgsConstructor
public class Admin extends Entity {
	private String name;
	private String surname;
	private String profilePicture;
	boolean active;
	@OneToOne
	private Credentials credentials;
}

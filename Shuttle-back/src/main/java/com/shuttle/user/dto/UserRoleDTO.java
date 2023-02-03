package com.shuttle.user.dto;

import com.shuttle.security.Role;
import com.shuttle.user.GenericUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO extends UserDTONoPassword {
    String role;

    public UserRoleDTO(GenericUser user) {
        super(user);
        role = user.getRoles().get(0).getName();
    }
}

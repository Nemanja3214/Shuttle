package com.shuttle.user.dto;

import com.shuttle.user.GenericUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChatDataDTO {
    String email;
    String primaryRole;
    String pfp;

    public UserChatDataDTO(GenericUser user){
        pfp = user.getProfilePicture();
        email = user.getEmail();
        primaryRole = user.getRoles().get(0).toString();
    }
}

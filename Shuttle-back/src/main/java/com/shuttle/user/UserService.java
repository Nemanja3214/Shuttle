package com.shuttle.user;

import com.shuttle.user.dto.BasicUserInfoDTO;
import com.shuttle.user.dto.UserDTO;

import java.util.List;

public interface UserService {
    GenericUser findById(Long id);
    GenericUser findByEmail(String email);
    List<GenericUser> findAll ();
     GenericUser save(UserDTO userDTO);
     GenericUser save(GenericUser user);
    GenericUser encodeUserPassword(GenericUser user, String password);
}

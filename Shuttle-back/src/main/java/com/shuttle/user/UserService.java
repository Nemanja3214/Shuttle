package com.shuttle.user;

import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.user.dto.BasicUserInfoDTO;
import com.shuttle.user.dto.UserDTO;

import java.io.IOException;
import java.util.List;

public interface UserService {
    GenericUser findById(Long id);
    GenericUser findByEmail(String email);
    List<GenericUser> findAll ();
    GenericUser save(UserDTO userDTO);
    GenericUser save(GenericUser user);
    GenericUser encodeUserPassword(GenericUser user, String password);
	GenericUser setActive(GenericUser user, boolean b);
	boolean getActive(GenericUser user);
	String getProfilePicture(long id) throws NonExistantUserException, IOException;
}

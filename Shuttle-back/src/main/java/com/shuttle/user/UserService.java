package com.shuttle.user;

import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.security.Role;
import com.shuttle.user.dto.BasicUserInfoDTO;
import com.shuttle.user.dto.UserDTO;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;

public interface UserService {
    GenericUser findById(Long id);
    GenericUser findByEmail(String email);
    List<GenericUser> findAll ();
    GenericUser save(UserDTO userDTO);
    GenericUser save(GenericUser user);
	GenericUser setActive(GenericUser user, boolean b);
	boolean getActive(GenericUser user);
	String getProfilePicture(long id) throws NonExistantUserException, IOException;

    /**
     * Fetch all GenericUser instances which have the given role (as string).
     * @param role Role name. Case sensistive: ('admin', 'driver', 'passenger').
     * @return List of users. Can be empty.
     */
    List<GenericUser> findByRole(String role);

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_PASSENGER = "passenger";
    public static final String ROLE_DRIVER = "driver";


    boolean isAdmin(GenericUser user);
    boolean isPassenger(GenericUser user);
    boolean isDriver(GenericUser user);
    
    /**
     * Change the current password of the given user.
     * @param user The user.
     * @param password New password as plain-text.
     * @return The user with an updated password.
     */
    GenericUser changePassword(GenericUser user, String password);
    
    /**
     * Compares the current password with the given password.
     * @param user The user.
     * @param password The password as plain-text.
     * @return True if they match, false otherwise.
     */
    boolean hasPassword(GenericUser user, String password);
    
    
	List<GenericUser> findAll(Pageable pageable);
}

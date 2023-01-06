package com.shuttle.user;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shuttle.common.FileUploadUtil;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.driver.Driver;
import com.shuttle.security.Role;
import com.shuttle.security.RoleService;
import com.shuttle.user.dto.UserDTO;
import com.shuttle.workhours.IWorkHoursService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private IWorkHoursService workHoursService;


    @Override
    public GenericUser findByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public GenericUser findById(Long id) throws AccessDeniedException {
        return userRepository.findById(id).orElseGet(null);
    }

    public List<GenericUser> findAll() throws AccessDeniedException {
        return userRepository.findAll();
    }

    @Override
    public GenericUser save(UserDTO userRequest) {
        GenericUser u = new GenericUser();
//        u.setName(userRequest.getUsername());

         u.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        u.setName(userRequest.getName());
        u.setSurname(userRequest.getSurname());
        u.setEnabled(true);
        u.setEmail(userRequest.getEmail());

         List<Role> roles = roleService.findByName("USER");
        u.setRoles(roles);

        return this.userRepository.save(u);
    }

    public GenericUser save(GenericUser user) {
        return this.userRepository.save(user);
    }

    public GenericUser encodeUserPassword(GenericUser user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

	@Override
	public GenericUser setActive(GenericUser user, boolean active) {
		final boolean newActiveState = active != user.getActive();
		user.setActive(active);
		userRepository.save(user);
		
		// If user is a driver, update working hours.
		// But only do this if the activity has changed.
		// Example: you are logged in and you log in again:
		// this method should not fire.
		
		if (newActiveState) {	
			for (Role r : user.getRoles()) {
				// TODO: Hardcoded!
				if (r.getName().equals("driver")) {
					if (active) {
						workHoursService.addNew((Driver)user);
					} else {
						workHoursService.finishLast((Driver)user);
					}
					break;
				}
			}
		}
	
		return user;
	}

	@Override
	public boolean getActive(GenericUser user) {
		return user.getActive();
	}

	@Override
	public String getProfilePicture(long id) throws NonExistantUserException, IOException {
		Optional<GenericUser> user = this.userRepository.findById(id);
		if(user.isEmpty()) {
			throw new NonExistantUserException();
		}
		String result = FileUploadUtil.getImageBase64(FileUploadUtil.profilePictureUploadDir, user.get().getProfilePictureName());
		return result;
	}
  
    @Override
    public List<GenericUser> findByRole(String role) {
        List<Role> roles = roleService.findByName(role);
        if (roles.size() == 0) {
            return new ArrayList<GenericUser>();
        }
        Role r = roles.get(0);

        return userRepository.findByRoles(r);
    }
}

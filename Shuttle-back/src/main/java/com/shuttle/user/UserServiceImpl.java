package com.shuttle.user;

import com.shuttle.security.RoleService;
import com.shuttle.driver.Driver;
import com.shuttle.security.Role;
import com.shuttle.user.dto.UserDTO;
import com.shuttle.workhours.IWorkHoursService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
		user.setActive(active);
		userRepository.save(user);
		
		// If user is a driver, update working hours.
		
		for (Role r : user.getRoles()) {
			// TODO: Hardcoded!
			System.err.println(r.getName());
			if (r.getName().equals("driver")) {
				System.err.println(r.getName());
				if (active) {
					workHoursService.addNew((Driver)user);
				} else {
					workHoursService.finishLast((Driver)user);
				}
				break;
			}
		}
	
		return user;
	}
}

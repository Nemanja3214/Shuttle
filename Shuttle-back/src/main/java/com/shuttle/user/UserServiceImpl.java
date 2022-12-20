package com.shuttle.user;

import com.shuttle.security.RoleService;
import com.shuttle.security.Role;
import com.shuttle.user.dto.UserDTO;
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

        // pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
        // treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
        u.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        u.setName(userRequest.getName());
        u.setSurname(userRequest.getSurname());
        u.setEnabled(true);
        u.setEmail(userRequest.getEmail());

        // u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER
        List<Role> roles = roleService.findByName("ROLE_USER");
        u.setRoles(roles);

        return this.userRepository.save(u);
    }
}

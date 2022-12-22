package com.shuttle;

import com.shuttle.credentials.CredentialsService;
import com.shuttle.credentials.ICredentialsRepository;
import com.shuttle.security.RoleService;
import com.shuttle.security.RoleServiceImpl;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @Autowired
    CredentialsService credentialsService;

    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void index() {
        credentialsService.insert("admin@gmail.com","admin");
        credentialsService.insert("driver@gmail.com","driver");
        credentialsService.insert("passenger@gmail.com","passenger");

        for (GenericUser user :
                userService.findAll()) {
            userService.encodeUserPassword(user, "sifra123");
            System.out.println(user.getPassword());
            userService.save(user);
        }

    }

}

package com.shuttle;

import com.shuttle.security.RoleService;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SwaggerController {


    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void index() {
        for (GenericUser user :
                userService.findAll()) {
            userService.encodeUserPassword(user, user.getPassword());
            userService.save(user);
        }

    }

}

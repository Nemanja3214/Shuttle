package com.shuttle;

import com.shuttle.credentials.CredentialsService;
import com.shuttle.credentials.ICredentialsRepository;
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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void  index() {
        credentialsService.insert("admin@gmail.com","admin");
        credentialsService.insert("driver@gmail.com","driver");
        credentialsService.insert("passenger@gmail.com","passenger");
    }

}

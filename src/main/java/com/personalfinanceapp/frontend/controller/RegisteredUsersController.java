package com.personalfinanceapp.frontend.controller;

import java.util.*;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.personalfinanceapp.frontend.service.RegisteredUsersService;
import com.personalfinanceapp.frontend.model.LoginForm;
import com.personalfinanceapp.frontend.model.Token;
import com.personalfinanceapp.frontend.model.UserSession;
import com.personalfinanceapp.frontend.model.RegisteredUsers;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT; 


@Controller
@RequestMapping(value = "/admin")
public class RegisteredUsersController {

    @Autowired
    RegisteredUsersService registeredUsersService;

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        // UserSession user = (UserSession) session.getAttribute("usersession");
        // if(user != null){
        //     RegisteredUsers userRole = user.getRegisteredUsers();
        //     Optional<Role> admin = userRole.getRoleSet().stream().filter(role->role.getName()=="Admin").findFirst();
        //     if(admin != null){
        //         return "redirect:/admin/welcome";
        //     }
        //     else{
        //         model.addAttribute("user", new LoginForm());
        //         return "login";
        //     }
        // }
        // else{
        //     model.addAttribute("user", new LoginForm());
        //     return "login";
        // }
        model.addAttribute("user", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") @Valid LoginForm lg, BindingResult bindingResult, Model model,
    HttpSession session){


        Token tk = registeredUsersService.login(lg.getEmail(), lg.getPassword());

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(tk.getAccess_token());
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

        if(Arrays.stream(roles).anyMatch("Admin"::contains)){
            UserSession userSession = new UserSession();
            userSession.setToken(tk);
    
            RegisteredUsers user = registeredUsersService.addAdminToSession(lg.getEmail(), tk.getAccess_token());
            userSession.setRegisteredUsers(user);
            userSession.setTokentime(System.currentTimeMillis()+ 10 * 1000);
            session.setAttribute("usersession", userSession);
            
            return "redirect:/admin/dashboard";
        }
        else{
            return "redirect:/admin/login";
        }
       
    }

    @GetMapping("/welcome")
    public String welcome(Model model, HttpSession session){
        UserSession user =(UserSession) session.getAttribute("usersession");

        
        RegisteredUsers rUser = user.getRegisteredUsers();
        model.addAttribute("userD", rUser);
        return "welcomePage";
          

    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        
        UserSession user =(UserSession) session.getAttribute("usersession");
        registeredUsersService.logout(user.getRegisteredUsers().getId(), user.getToken().getAccess_token());
        session.invalidate();
        return "redirect:/admin/login";
    }

    
}

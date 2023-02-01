package com.personalfinanceapp.frontend.inteceptor;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.personalfinanceapp.frontend.exception.UnauthorizedException;
import com.personalfinanceapp.frontend.model.Token;
import com.personalfinanceapp.frontend.model.UserSession;
import com.personalfinanceapp.frontend.service.RegisteredUsersService;



@Component
public class SecurityInterceptor implements HandlerInterceptor {

  @Autowired
  RegisteredUsersService registeredUsersService;

  @Override
  public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, Object handler) 
                               throws IOException, UnauthorizedException {
    System.out.println("Intercepting " + request.getRequestURI());
    
    String uri = request.getRequestURI();
    
    if (uri.equalsIgnoreCase("/login")) {
      return true;
    }

    
    
    // if (uri.startsWith("/home/")) {
    //   return true;
    // }
    
    HttpSession session = request.getSession();
    UserSession userSession = (UserSession) session.getAttribute("usersession");


    if (userSession == null) {
      response.sendRedirect("/login");
      return false;
    }


    String email = userSession.getRegisteredUsers().getEmail();
    String token = userSession.getToken().getAccess_token();
    //check jwt token is expire or not
    if(userSession != null){

      try{
        registeredUsersService.checkToken(email, token);
      }
      catch(Exception e){
        response.sendRedirect("/login");
        return false;
      }
    }
    if(userSession.getTokentime()<= System.currentTimeMillis()){
      Token refreshToken = registeredUsersService.refreshToken(token);
      registeredUsersService.addAdminToSession(userSession.getRegisteredUsers().getEmail(), refreshToken.getAccess_token());
      userSession.setTokentime(System.currentTimeMillis()+ 50 * 60 * 1000);
      userSession.setToken(refreshToken);
    }

    
    // List<String> userRoles = userSession.getUser().getRoleIds();
    
    // if (uri.startsWith("/admin") && !userRoles.contains("admin")) {
    //   throw new UnauthorizedException();
    // }
    
    // if (uri.startsWith("/staff") && !userRoles.contains("staff")) {
    //   throw new UnauthorizedException();
    // }
    
    
    // OK, forward to Controller
    return true;
  }
}

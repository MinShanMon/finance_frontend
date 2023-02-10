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
    
    

    
    
    
    // if (uri.startsWith("/home/")) {
    //   return true;
    // }
    
    HttpSession session = request.getSession();
    UserSession userSession = (UserSession) session.getAttribute("usersession");

    if (uri.equalsIgnoreCase("/admin/login")) {
      return true;
    }

    if (userSession == null) {
      response.sendRedirect("/admin/login");
      return false;
    }

  
    String email = userSession.getRegisteredUsers().getEmail();
    Integer id = userSession.getRegisteredUsers().getId();
    String token = userSession.getToken().getAccess_token();
    //check jwt token is expire or not
    if(userSession != null){

      try{
        registeredUsersService.checkToken(id, token);
        // response.sendRedirect("/admin/welcome");
      }
      catch(Exception e){
        response.sendRedirect("/admin/login");
        return false;
      }
    }
    if (uri.equalsIgnoreCase("/admin/login") && userSession != null) {
      response.sendRedirect("/admin/welcome");
      return true;
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

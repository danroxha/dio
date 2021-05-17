package projeto.azure.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.algorithms.Algorithm;
//import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;



import projeto.azure.jwt.data.UserData;
import projeto.azure.jwt.security.SecurityConstants;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  
  private final AuthenticationManager authenticationManager;

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override 
  public Authentication attemptAuthentication(
    HttpServletRequest request, HttpServletResponse response ) throws AuthenticationException 
  {
    try {
      UserData creds = new ObjectMapper()
        .readValue(request.getInputStream(), UserData.class);

      return authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          creds.getUserName(),
          creds.getPassword(),
          new ArrayList<>()
        )
      );
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  
  @Override
  public void successfulAuthentication(
    HttpServletRequest req, HttpServletResponse res, 
    FilterChain chain, Authentication auth
  ) throws IOException, ServletException 
  {
    String token = JWT.create()
      .withSubject(((User) auth.getPrincipal()).getUsername())
      .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
      .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

    res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    
  }
  
}
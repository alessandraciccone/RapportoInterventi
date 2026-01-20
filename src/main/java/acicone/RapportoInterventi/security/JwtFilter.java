package acicone.RapportoInterventi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {
private final JwtTool jwtTool;
private final CustomUserDetailsService userDetailsService;
public JwtFilter(JwtTool jwtTool, CustomUserDetailsService userDetailsService) {
    this.jwtTool = jwtTool;
    this.userDetailsService = userDetailsService;
}
@Override
protected void doFilterInternal(HttpServletRequest request,
                               HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException{
   String header = request.getHeader("Authorization");
   if(header != null && header.startsWith("Bearer")){
       String token = header.substring(7);
if (jwtTool.validateToken(token)){
    UUID userId = jwtTool.getUserIdFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserById(userId);
    UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


}
   }
    filterChain.doFilter(request, response);
}
}

package blockchain.medicalRecords.HealthCareData.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import blockchain.medicalRecords.HealthCareData.util.JwtUtil;
import blockchain.medicalRecords.HealthCareData.services.MyUserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException {

        try {
            String authorizeHeader = null;
            String username = null;
            String jwt = null;

            Cookie[] cookies = req.getCookies();
            if(cookies != null) {
                for(Cookie c : cookies) {
                    if(c.getName().equals("abcHospitalApp")) {
                        authorizeHeader = c.getValue();
                    }
                    if(c.getName().equals("abcHospitalAppUser")) {
                        username = c.getValue();
                    }

                }
            } else {
                System.out.println("null cookies");
            }

            if(authorizeHeader != null /*&& authorizeHeader.startsWith("Bearer ")*/) {
                jwt = authorizeHeader;
                username = jwtUtil.extractUsername(jwt);
            }

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if(jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            chain.doFilter(req, res);
        } catch (IOException io) {
            System.out.println("doFilterInterval IOException " + io);
            try {
                res.sendRedirect("/login");
            } catch (Exception e) {
                System.out.println("doFilterInternal : "+ e);
            }
        }

    }
}

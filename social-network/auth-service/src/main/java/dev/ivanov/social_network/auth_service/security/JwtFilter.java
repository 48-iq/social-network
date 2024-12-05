package dev.ivanov.social_network.auth_service.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.exceptions.AccountNotFoundException;
import dev.ivanov.social_network.auth_service.exceptions.ActionWithDeletedAccountException;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import dev.ivanov.social_network.auth_service.services.JwtUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String access = authHeader.substring(7);

                Map<String, Claim> claims = jwtUtils.verifyAccessAndRetrieveClaims(access);
                String accountId = claims.get("id").asString();

                Account account = accountRepository.findById(accountId)
                        .orElseThrow(AccountNotFoundException::new);

                if (account.getDeleted())
                    throw new ActionWithDeletedAccountException();

                Authentication authentication = new UsernamePasswordAuthenticationToken(new UserDetailsImpl(account), null);
                if (SecurityContextHolder.getContext().getAuthentication() == null)
                    SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            response.getWriter().write("jwt verification error");
        } catch (AuthenticationException e) {
            response.getWriter().write("authentication error");
        } catch (AccountNotFoundException e) {
            response.getWriter().write("account doesn't exist");
        } catch (ActionWithDeletedAccountException e) {
            response.getWriter().write("account has been deleted");
        }

    }
}

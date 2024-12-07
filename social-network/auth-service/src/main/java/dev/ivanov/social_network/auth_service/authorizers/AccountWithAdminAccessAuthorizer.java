package dev.ivanov.social_network.auth_service.authorizers;

import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import dev.ivanov.social_network.auth_service.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;


@Component
public class AccountWithAdminAccessAuthorizer implements AuthorizationManager<RequestAuthorizationContext> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                       RequestAuthorizationContext requestAuthorizationContext) {
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Account account = userDetails.getAccount();
        if (account == null)
            return new AuthorizationDecision(false);

        if (account.getRoles() != null && account.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN")))
            return new AuthorizationDecision(true);

        HttpServletRequest request = requestAuthorizationContext.getRequest();
        Map<String, String> variables = requestAuthorizationContext.getVariables();
        String accountId = variables.get("accountId");

        if (account.getId() == null || !account.getId().equals(accountId))
            return new AuthorizationDecision(false);

        return new AuthorizationDecision(true);
    }
}

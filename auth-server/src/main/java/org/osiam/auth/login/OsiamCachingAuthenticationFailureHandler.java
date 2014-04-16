package org.osiam.auth.login;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osiam.security.helper.LoginDecisionFilter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.google.common.base.Strings;

public class OsiamCachingAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public static final String LAST_USERNAME_KEY = "LAST_USERNAME";
    public static final String LAST_PROVIDER_KEY = "LAST_PROVIDER";

    @Inject
    private LoginDecisionFilter loginDecisionFilter;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        super.onAuthenticationFailure(request, response, exception);

        HttpSession session = request.getSession(false);
        if (session != null || isAllowSessionCreation()) {
            String usernameParameter = loginDecisionFilter.getUsernameParameter();
            String lastUserName = request.getParameter(usernameParameter);
            request.getSession().setAttribute(LAST_USERNAME_KEY, lastUserName);

            String provider = request.getParameter("provider");
            provider = Strings.isNullOrEmpty(provider) ? "internal" : provider;
            request.getSession().setAttribute(LAST_PROVIDER_KEY, provider);
        }
    }
}
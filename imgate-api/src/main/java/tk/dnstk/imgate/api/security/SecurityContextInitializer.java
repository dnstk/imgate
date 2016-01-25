package tk.dnstk.imgate.api.security;

import javax.servlet.http.HttpServletRequest;

public interface SecurityContextInitializer {

    void initializeContext(SecurityContext context, HttpServletRequest request);

}

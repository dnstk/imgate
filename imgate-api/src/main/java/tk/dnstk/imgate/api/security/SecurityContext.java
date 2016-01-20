package tk.dnstk.imgate.api.security;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.EnumMap;
import java.util.Map;

public class SecurityContext {


    private static final String ATTR_NAME = SecurityContext.class.getName();

    private static ThreadLocal<SecurityContext> context = new ThreadLocal<>();

    private int deepth = 0;

    private Map<SecurityValue, String> values = new EnumMap<>(SecurityValue.class);

    private SecurityContext() {
    }

    public static SecurityContext getContext() {
        SecurityContext securityContext = context.get();
        if (securityContext == null) {
            throw new IllegalStateException("No security context initialized");
        } else {
            return securityContext;
        }
    }

    static SecurityContext createFromRequest(WebRequest request) {
        Object attribute = request.getAttribute(ATTR_NAME, RequestAttributes.SCOPE_REQUEST);
        SecurityContext securityContext;
        if (attribute instanceof SecurityContext) {
            securityContext = (SecurityContext) attribute;
        } else {
            securityContext = new SecurityContext();
            securityContext.populate(request);
            request.setAttribute(ATTR_NAME, securityContext, RequestAttributes.SCOPE_REQUEST);
        }
        return securityContext;
    }

    static SecurityContext getFromRequest(WebRequest request) {
        Object attribute = request.getAttribute(ATTR_NAME, RequestAttributes.SCOPE_REQUEST);
        if (attribute instanceof SecurityContext) {
            return (SecurityContext) attribute;
        } else {
            // unexpected
            throw new IllegalStateException("null or invalid api context in the request");
        }
    }

    private void populate(WebRequest request) {
        SecurityValue[] list = SecurityValue.values();
        for (SecurityValue ap : list) {
            String value = resolveSecurityValue(request, ap);
            if (value != null) {
                values.put(ap, value);
            }
        }
    }

    private String resolveSecurityValue(WebRequest request, SecurityValue ap) {
        // TODO
        return null;
    }

    public String get(SecurityValue ap) {
        return values.get(ap);
    }

    void bind() {
        SecurityContext securityContext = context.get();
        if (securityContext == null) {
            // empty, normal case
            context.set(this);
            this.deepth++;
        } else if (securityContext == this) {
            // nested case
            this.deepth++;
        } else {
            // unexpected
            throw new IllegalStateException("another api context has bound on current thread");
        }
    }

    void unbind() {
        if (context.get() == this) {
            if (--this.deepth <= 0) {
                context.remove();
            }
        } else {
            // unexpected case
            throw new IllegalStateException("The api context is not bound on current thread");
        }
    }

    @Override
    public String toString() {
        return "SecurityContext{" + "values=" + values + "}";
    }
}

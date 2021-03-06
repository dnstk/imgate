package tk.dnstk.imgate.api.security;

import tk.dnstk.imgate.api.InvalidAccessException;

import javax.servlet.http.HttpServletRequest;
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

    public static void runInContext(Runnable runnable) {
        if (context.get() == null) {
            SecurityContext sc = new SecurityContext();
            sc.bind();
            try {
                runnable.run();
            } finally {
                sc.unbind();
            }
        } else {
            runnable.run();
        }
    }

    static SecurityContext createFromRequest(HttpServletRequest request) {
        Object attribute = request.getAttribute(ATTR_NAME);
        SecurityContext securityContext;
        if (attribute instanceof SecurityContext) {
            securityContext = (SecurityContext) attribute;
        } else {
            securityContext = new SecurityContext();
            securityContext.populate(request);
            request.setAttribute(ATTR_NAME, securityContext);
        }
        return securityContext;
    }

    static SecurityContext getFromRequest(HttpServletRequest request) {
        Object attribute = request.getAttribute(ATTR_NAME);
        if (attribute instanceof SecurityContext) {
            return (SecurityContext) attribute;
        } else {
            // unexpected
            throw new IllegalStateException("null or invalid api context in the request");
        }
    }

    // quick method
    public static String currentValue(SecurityValue value) {
        String ret = getContext().get(value);
        if (ret == null) {
            throw new InvalidAccessException("No " + value + " found");
        }
        return ret;
    }

    private void populate(HttpServletRequest request) {
        // get value from header
        SecurityValue[] list = SecurityValue.values();
        for (SecurityValue ap : list) {
            String value = resolveSecurityValue(request, ap);
            if (value != null) {
                set(ap, value);
            }
        }
    }

    private String resolveSecurityValue(HttpServletRequest request, SecurityValue ap) {
        String headerName = ap.getHeaderName();
        if (headerName != null) {
            return request.getHeader(headerName);
        } else {
            return null;
        }
    }

    public String get(SecurityValue ap) {
        return values.get(ap);
    }

    public void set(SecurityValue ap, String value) {
        values.put(ap, value);
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

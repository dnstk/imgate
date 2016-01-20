package tk.dnstk.imgate.api.security;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;

public class SecurityContextWebInterceptor implements AsyncWebRequestInterceptor {

    @Override
    public void preHandle(WebRequest request) throws Exception {
        SecurityContext context = SecurityContext.createFromRequest(request);
        context.bind();
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        SecurityContext context = SecurityContext.getFromRequest(request);
        context.unbind();
    }

    @Override
    public void afterConcurrentHandlingStarted(WebRequest request) {
        SecurityContext context = SecurityContext.getFromRequest(request);
        context.unbind();
    }

}

package tk.dnstk.imgate.api.security;

import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityContextWebInterceptor implements AsyncHandlerInterceptor {

    private SecurityContextInitializer sci;

    public SecurityContextWebInterceptor(SecurityContextInitializer sci) {
        this.sci = sci;
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext context = SecurityContext.getFromRequest(request);
        context.unbind();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext context = SecurityContext.createFromRequest(request);
        sci.initializeContext(context, request);
        context.bind();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityContext context = SecurityContext.getFromRequest(request);
        context.unbind();
    }
}

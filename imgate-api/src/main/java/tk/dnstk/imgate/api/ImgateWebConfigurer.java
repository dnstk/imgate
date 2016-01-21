package tk.dnstk.imgate.api;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import tk.dnstk.imgate.api.security.SecurityContextWebInterceptor;

@Component
public class ImgateWebConfigurer extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(new SecurityContextWebInterceptor());
    }

}

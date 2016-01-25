package tk.dnstk.imgate.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class SecurityWebConfigurer extends WebMvcConfigurerAdapter {

    @Autowired
    private SecurityContextInitializer sci;

    private AuthorizedOnConverterHook hook = new AuthorizedOnConverterHook();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityContextWebInterceptor(sci));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(hook);
    }
}

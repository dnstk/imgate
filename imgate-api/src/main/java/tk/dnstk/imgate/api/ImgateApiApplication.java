package tk.dnstk.imgate.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@SpringBootApplication
@EnableSwagger2
public class ImgateApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImgateApiApplication.class, args);
    }

    @Bean
    public Docket imgateApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("imgate-api")
                .select()
                .apis(basePackage(getClass().getPackage().getName()))
                .build();
    }

}

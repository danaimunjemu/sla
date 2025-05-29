package zw.co.afc.orbit.sla.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Allow all endpoints, you can specify a specific endpoint pattern here
                        .allowedOrigins("*") // Allow requests from any origin, you can modify it to allow specific origins
                        .allowedMethods("*") // Allow specific HTTP methods
                        .allowedHeaders("*"); // Allow all headers in the request
            }
        };
    }
}
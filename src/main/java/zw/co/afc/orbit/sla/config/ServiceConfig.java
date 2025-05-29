package zw.co.afc.orbit.sla.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ServiceConfig {
    @Value("${api.service.orbit-user-service.url}")
    private String orbitUserBaseUrl;

    @Value("${api.service.orbit-user-service.endpoint.search}")
    private String userSearchEndpoint;

    public String userSearchUrl() {
        return orbitUserBaseUrl + userSearchEndpoint;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

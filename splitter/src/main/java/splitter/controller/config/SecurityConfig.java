package splitter.controller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception {
        chainBuilder.authorizeRequests(configurer -> {
            try {
                configurer.antMatchers("/api/**")
                        .permitAll()
                        .and()
                        .csrf()
                        .disable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        chainBuilder.authorizeRequests(
                        configurer -> configurer
                                .anyRequest().authenticated()
                ).oauth2Login(Customizer.withDefaults());
        return chainBuilder.build();
    }
}

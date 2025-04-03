package com.tsore.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("development")
public class DevelopmentSecurityConfig implements WebMvcConfigurer {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/**"));
    }

    // Méthode pour autoriser CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permettre CORS pour toutes les origines
        registry.addMapping("/**")
                .allowedOrigins("*") // Autoriser toutes les origines
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Méthodes HTTP autorisées
                .allowedHeaders("*") // Autoriser tous les en-têtes
                .allowCredentials(false); // Vous pouvez ajuster cela selon vos besoins (faux si vous ne voulez pas de
                                          // cookies ou de données d'authentification)
    }

}
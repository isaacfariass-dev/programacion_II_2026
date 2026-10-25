package com.zebop.sistemasCorrelativas.config;

import com.zebop.sistemasCorrelativas.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración MVC: registra el interceptor de autenticación
 * y define las rutas públicas que no requieren sesión activa.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Registra el AuthInterceptor sobre todas las rutas,
     * excluyendo las públicas (login, recursos estáticos).
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/favicon.ico",
                    "/error"
                );
    }
}

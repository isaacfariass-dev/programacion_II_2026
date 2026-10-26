package com.zebop.sistemasCorrelativas;

import com.zebop.sistemasCorrelativas.interceptor.AuthInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Clase principal: arranca la aplicación y registra el interceptor de
// autenticación directamente (ya no existe un paquete config separado).
@SpringBootApplication
public class SistemasCorrelativasApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SistemasCorrelativasApplication.class, args);
	}

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

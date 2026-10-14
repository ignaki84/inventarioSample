package com.idm.demos.inventario.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthFilter;
	
//	@Value("${cors.allowed-origins}")
//    private List<String> allowedOrigins;
	
	@Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(Customizer.withDefaults())
	        .csrf(csrf -> csrf.disable()) // Deshabilitado porque usamos JWT (Stateless)
	        .authorizeHttpRequests(auth -> auth
	            // Rutas públicas permitidas (Swagger y UI)
	            .requestMatchers(
	                    "/v3/api-docs/**",
	                    "/swagger-ui/**",
	                    "/swagger-ui.html"
	            ).permitAll()
	            .requestMatchers("/api/v1/auth/**" // Endpoint para hacer login y obtener el token)
	            ).permitAll()
	            .requestMatchers("/error").permitAll()
	            // Cualquier otra ruta requiere autenticación
	            .anyRequest().authenticated()
	        )
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
	
	    return http.build();
	}
	
	@Bean AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        //return authConfig.getAuthenticationManager();
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
//        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
	
	@Bean PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	@Bean FilterRegistrationBean<JwtAuthenticationFilter> preventDuplicateJwtFilterRegistration(JwtAuthenticationFilter filter) {
	    FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
	    registration.setEnabled(false);
	    return registration;
	}
	
	@Bean CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    
	    // Define el origen exacto de tu frontend (ej. Angular corriendo en el puerto 4200)
	    configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200")); 
	    
	    // Permite los métodos HTTP estándar y el OPTIONS que es crucial para el preflight
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    
	    // Autoriza explícitamente el envío del JWT en la cabecera
	    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
	    
	    // Necesario si las peticiones del cliente incluyen credenciales
	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    
	    // Aplica esta política a todos los endpoints de tu API
	    source.registerCorsConfiguration("/**", configuration); 
	    
	    return source;
	}
}

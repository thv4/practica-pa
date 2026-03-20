package es.udc.paproject.backend.rest.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtGenerator jwtGenerator;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(Customizer.withDefaults())
				.csrf((csrf) -> csrf.disable())
				.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new JwtFilter(jwtGenerator), UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests((authorize) -> authorize
						// ===== Endpoints públicos (sin autenticación) =====
						.requestMatchers(HttpMethod.POST, "/users/signUp").permitAll()
						.requestMatchers(HttpMethod.POST, "/users/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/users/loginFromServiceToken").permitAll()

						// ===== FUNC-1: Cartelera (pública) =====
						.requestMatchers(HttpMethod.GET, "/carteleras/**").permitAll()

						// ===== FUNC-2: Detalle película (público) =====
						.requestMatchers(HttpMethod.GET, "/pelicula/**").permitAll()

						// ===== FUNC-3: Detalle sesión (público) =====
						.requestMatchers(HttpMethod.GET, "/sesion/**").permitAll()

						// ===== FUNC-4: Comprar entradas (solo VIEWER) =====
						.requestMatchers(HttpMethod.POST, "/compras/**").hasRole("ESPECTADOR")

						// ===== FUNC-5: Histórico de compras (solo VIEWER) =====
						.requestMatchers(HttpMethod.GET, "/compras/compras").hasRole("ESPECTADOR")

						// ===== FUNC-6: Entregar entradas (solo TICKET_SELLER) =====
						.requestMatchers(HttpMethod.POST, "/entregas/entregar").hasRole("TAQUILLERO")

						// ===== Perfil y contraseña (ambos roles) =====
						.requestMatchers(HttpMethod.PUT, "/users/*").hasAnyRole("ESPECTADOR", "TAQUILLERO")
						.requestMatchers(HttpMethod.POST, "/users/*/changePassword").hasAnyRole("ESPECTADOR", "TAQUILLERO")
						.requestMatchers("/error").permitAll()

						// ===== Cualquier otra petición, denegada =====
						.anyRequest().denyAll()
				);

		return http.build();

	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration config = new CorsConfiguration();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		config.setAllowCredentials(true);
		config.setAllowedOriginPatterns(Arrays.asList("*"));
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		source.registerCorsConfiguration("/**", config);

		return source;

	}

}
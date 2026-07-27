package mx.edu.utez.JuventudxTemixco.Security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
/**
 * Cadena de seguridad stateless: JWT, CORS centralizado, errores 401/403 en JSON.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager
    authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "https://juventudxtemixco.org",
                "https://www.juventudxtemixco.org"
        ));
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new

                UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService
                                                                   jwtService) {

        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(

            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler) throws

            Exception {
        http

                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))

                .addFilterBefore(jwtAuthenticationFilter,

                        UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // LOGIN ----
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/recuperar-password").permitAll()
                        .requestMatchers("/api/auth/actualizarPassword").permitAll()
                        .requestMatchers("/api/auth/validarCodigo").permitAll()

                        // ADMIN ----
                        .requestMatchers(HttpMethod.GET, "/api/admin/**"). hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/**").hasRole("ADMIN")

                        // AFILIADOS ----
                        .requestMatchers(HttpMethod.GET, "/api/afiliados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/afiliados/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/afiliados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/afiliados/**").hasRole("ADMIN")

                        // BENEFICIARIOS
                        .requestMatchers("/api/beneficiarios/Municipios").permitAll()
                        .requestMatchers("/api/beneficiarios/verificarCorreo").permitAll()
                        .requestMatchers("/api/beneficiarios/filtro").hasRole("ADMIN")
                        .requestMatchers("/api/beneficiarios/buscar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/beneficiarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/beneficiarios/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/beneficiarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/beneficiarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/beneficiarios/pdf").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/beneficiario/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/afiliado/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/afiliado/pdf").permitAll()

                        // ALIANZAS ----
                        .requestMatchers(HttpMethod.GET, "/api/Alianza/busquedaAlianza").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/Alianza/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/Alianza/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/Alianza/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/Alianza/**"). hasRole("ADMIN")

                        // GOAL ----
                        .requestMatchers(HttpMethod.GET, "/api/goal").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/goal/name/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/goal").hasRole("ADMIN")


                        // SECTIONS ---
                        .requestMatchers(HttpMethod.GET, "/api/section/**"). permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/section").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/section/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/section/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/section/**").hasRole("ADMIN")

                        // PROGRAMS ----
                        .requestMatchers(HttpMethod.GET, "/api/program/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/program/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/program/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/program/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/program/**").hasRole("ADMIN")

                        // DONATIONS ----
                        .requestMatchers(HttpMethod.GET, "/api/Donacion/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/Donacion/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/Donacion/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/Donacion/**").hasRole("ADMIN")


                        .anyRequest().authenticated());

        return http.build();
    }
}

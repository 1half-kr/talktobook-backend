package com.lifelibrarians.lifebookshelf.config;

import com.lifelibrarians.lifebookshelf.auth.jwt.JwtAuthenticationConverter;
import com.lifelibrarians.lifebookshelf.auth.jwt.JwtRedisValidator;
import com.lifelibrarians.lifebookshelf.auth.jwt.MemberSessionAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

	private final AuthenticationManager jwtAuthenticationManager;
	private final AccessDeniedHandler jwtAccessDeniedHandler;
	private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAuthenticationConverter jwtAuthenticationConverter;
	private final JwtDecoder jwtDecoder;
	private final JwtRedisValidator jwtRedisValidator;

    @Value("${security.cors.domain}")
    private String corsDomain;

    @Value("${security.cors.ip}")
    private String corsIp;

    @Value("${security.cors.web}")
    private String corsWeb;

    @Value("${security.cors.android}")
    private String corsAndroid;

    @Value("${security.cors.ios}")
    private String corsIos;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//      @formatter:off
		return httpSecurity
				.cors()
					.configurationSource(corsConfigurationSource())
				.and()
				.csrf().disable()
					.authorizeRequests()
					.anyRequest().permitAll()
				.and()
				.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.oauth2ResourceServer()
					.bearerTokenResolver(new DefaultBearerTokenResolver())
					.jwt()
						.jwtAuthenticationConverter(jwtAuthenticationConverter)
						.decoder(jwtDecoder)
						.authenticationManager(jwtAuthenticationManager)
					.and()
					.accessDeniedHandler(jwtAccessDeniedHandler)
					.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.and()
				.formLogin().disable()
					.addFilterAfter(
							new MemberSessionAuthenticationFilter(jwtRedisValidator), BearerTokenAuthenticationFilter.class
					)
				.build();
//      @formatter:on
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(corsDomain, corsIp, corsWeb, corsAndroid, corsIos));
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
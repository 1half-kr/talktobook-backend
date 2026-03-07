package com.lifelibrarians.lifebookshelf.auth.password;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CustomPasswordEncoder {

	private static final String SALT = "$2a$10$DOWSDhFSU2UQ3V5cm4qz7e"; // 고정된 salt 값

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {
			@Override
			public String encode(CharSequence rawPassword) {
				return BCrypt.hashpw(rawPassword.toString(), SALT);
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
			}
		};
	}
}
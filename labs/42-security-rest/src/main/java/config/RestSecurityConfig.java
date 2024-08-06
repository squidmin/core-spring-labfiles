package config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class RestSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// @formatter:off
		http.authorizeHttpRequests((authz) -> authz
				.requestMatchers(HttpMethod.GET, "/accounts/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
				.requestMatchers(HttpMethod.PUT, "/accounts/**").hasAnyRole("ADMIN", "SUPERADMIN")
				.requestMatchers(HttpMethod.POST, "/accounts/**").hasAnyRole("ADMIN", "SUPERADMIN")
				.requestMatchers(HttpMethod.DELETE, "/accounts/**").hasAnyRole("SUPERADMIN")
				.requestMatchers(HttpMethod.GET, "/authorities").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
				.anyRequest().denyAll())
			.httpBasic(withDefaults())
			.csrf(CsrfConfigurer::disable);
        // @formatter:on

        return http.build();
	}

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
		// Add three users with corresponding roles:
		// (Make sure to store the password in encoded form.)

		// - "user"/"user" with "USER" role (example code is provided below)
		UserDetails user = User.withUsername("user").password(passwordEncoder.encode("user")).roles("USER").build();
		// - "admin"/"admin" with "USER" and "ADMIN" roles
		UserDetails admin = User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN").build();
		// - "superadmin"/"superadmin" with "USER", "ADMIN", and "SUPERADMIN" roles
		UserDetails superadmin = User.withUsername("superadmin").password(passwordEncoder.encode("superadmin")).roles("USER", "ADMIN", "SUPERADMIN").build();
    	// - pass all users in the InMemoryUserDetailsManager constructor
//		UserDetails spring = User.withUsername("spring").password(passwordEncoder.encode("spring")).roles("USER", "ADMIN").build(); // Adding the user "spring"
//		UserDetails testUser1 = User.withUsername("testuser1").password(passwordEncoder.encode("testuser1")).roles("USER", "ADMIN").build();

		return new InMemoryUserDetailsManager(
			user,
			admin,
			superadmin
//			testUser1,
//			spring
		);
	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

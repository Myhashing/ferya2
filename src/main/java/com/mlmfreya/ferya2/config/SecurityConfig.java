package com.mlmfreya.ferya2.config;


import com.mlmfreya.ferya2.repository.UserRepository;
import com.mlmfreya.ferya2.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;
    @Bean
    UserDetailsServiceImpl customUserDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                        "/assets/**","/plugins/**",
                                        "/css/**","/media/**",
                                        "/js/**","/",
                                        "/home", "/about",
                                        "/login", "/register",
                                        "/shop/**","/public/**",
                                        "/blog/**").permitAll()
//                                .requestMatchers("/admin/**").hasRole(User.Role.ADMIN.toString())
                                .anyRequest().authenticated()
                )
                .formLogin().loginPage("/login").defaultSuccessUrl("/dashboard").failureUrl("/login?error=true").permitAll() // Redirect to "/dashboard" after successful login
                .and()
                .logout().permitAll()
                .and()
                .csrf().disable();

        return http.build();
    }


}

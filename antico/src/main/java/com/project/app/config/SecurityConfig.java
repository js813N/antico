package com.project.app.config;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.project.app.common.AES256;
import com.project.app.common.Constants;
import com.project.app.security.CustomAccessHandler;
import com.project.app.security.CustomEntryPoint;
import com.project.app.security.LoginFailureHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	
	private final CustomAccessHandler custom_handler;
	
	
	private final CustomEntryPoint custom_entry_point;
	
	
	private final LoginFailureHandler login_failure_handler;
	
    

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	

	/* 
	 * Bcrypt bean 등록
	 */
    @Bean
    PasswordEncoder pwd_encoder() {
		return new BCryptPasswordEncoder();
	}
    
	/*
	 * AES256 암호화 클래스 bean 등록
	 */
	@Bean
	AES256 aes256() throws UnsupportedEncodingException {
		return new AES256(Constants.KEY);
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
	
		
    http.authorizeHttpRequests(
    		
    	  request -> request
    	  .requestMatchers("/chat/**").authenticated()
    	  
    	  .requestMatchers("/product/**").authenticated()
    	  
    	  .requestMatchers("/mypage/**").authenticated()
    	  
          .requestMatchers("/kakaologin/**").permitAll()
          
          .requestMatchers("/**").permitAll()
             
          .anyRequest().authenticated()
    )
    .exceptionHandling(ex -> ex
    	 .accessDeniedHandler(custom_handler)
    	 .authenticationEntryPoint(custom_entry_point)
    	 )
    .csrf(AbstractHttpConfigurer::disable)
    .formLogin((formLogin) ->
      	formLogin
	      	.loginPage("/member/login")
	      	.loginProcessingUrl("/auth/login")
	      	.usernameParameter("member_user_id")
	      	.passwordParameter("member_passwd")
	      	.defaultSuccessUrl("/index", true)
	      	.failureHandler(login_failure_handler)
      		.permitAll()
    	)
     .logout(logout -> logout
    		 .logoutUrl("/logout")
    		 .logoutSuccessUrl("/index")
    		 .clearAuthentication(true)
    		 );
     
     return http.build();
     }

	
	
	
}

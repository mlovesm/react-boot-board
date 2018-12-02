package com.example.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.example.boot.security.CustomUserDetailsService;
import com.example.boot.security.JwtAuthenticationEntryPoint;
import com.example.boot.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Autowired
    CustomUserDetailsService customUserDetailsService;
	
	@Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }
	
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/script/**", "image/**", "/fonts/**", "lib/**");
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        http
		        .cors()
		        .and()
		    .csrf()
		        .disable()
		    .exceptionHandling()
		        .authenticationEntryPoint(unauthorizedHandler)
		        .and()
		    .sessionManagement()
		        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		        .and()
	        .authorizeRequests()
	        	.antMatchers("/",
		            "/favicon.ico",
		            "/**/*.png",
		            "/**/*.gif",
		            "/**/*.svg",
		            "/**/*.jpg",
		            "/**/*.html",
		            "/**/*.css",
		            "/**/*.js")
		            .permitAll()
//                .antMatchers("/", "/oauth2/**", "/login/**",  "/css/**",
//                		"/images/**", "/js/**", "/console/**").permitAll()
		        .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
                	.permitAll()
                .anyRequest().authenticated()
	                
	            .and()
	                .headers().frameOptions().disable()	//XFrameOptionsHeaderWriter의 최적화 설정을 허용안함
	            .and()
	//            .and()
	//                .formLogin()
	//                .successForwardUrl("/board/list")
	//            .and()
	//                .logout()
	//                .logoutUrl("/logout")
	//                .logoutSuccessUrl("/")
	//                .deleteCookies("JSESSIONID")
	//                .invalidateHttpSession(true)
	                .addFilterBefore(filter, CsrfFilter.class)	//첫번째 인자보다 먼저 시작될 필터 등록
	                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        // Add our custom JWT security filter
    }
	
}

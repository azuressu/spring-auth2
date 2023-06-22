package com.sparta.springauth2.config;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // SpringSecurity 지원을 가능하게 함
public class WebSecurityConfig  {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        // resources 접근 허용 설정
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        // 이 요청들은 로그인, 회원가입 관련 요청으로 비회원/회원 상관없이 누구나 접근이 가능해야 함
                        // 인증이 필요없는 URL들을 간편하게 허가 가능
                        .requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                        // 인증이 필요한 URL도 간편하게 처리 가능
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 로그인 사용
        http.formLogin((formLogin) ->
                formLogin
                        // 로그인 View 제공(GET /api/user/login-page)
                        .loginPage("/api/user/login-page")
                        // 로그인 처리 (Post /api/user/login)
                        .loginProcessingUrl("/api/user/login")
                        // 로그인 처리 후 성공 시 URL
                        .defaultSuccessUrl("/")
                        // 로그인 처리 후 실패 시 URL
                        .failureUrl("/api/user/login-page?error")
                        .permitAll()
        );
        return http.build();
    }


}

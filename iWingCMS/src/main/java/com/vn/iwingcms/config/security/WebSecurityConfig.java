package com.vn.iwingcms.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean("userLoginSuccessHandler")
    public AuthenticationSuccessHandler userLoginSuccessHandler() {
        return new AppAuthenticationSuccessHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests()
                .antMatchers("/webjars/**", "/resources/**", "/assets/**", "/fonts/**", "/ws/**").permitAll()
                .antMatchers("/**")
                .hasAnyAuthority("Administrators", "Users", "SADMIN", "ADMIN", "REPORT", "CSKH", "TOPUP_CSKH", "TOPUP_ADMIN")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/error/403.html?authorization_error=true") //khi người dùng không đủ quyền truy cập thì sẽ redirect đến trang này
                .and()
//            .csrf().disable()
                .headers().frameOptions().disable().and()
                .formLogin()
                .usernameParameter("j_username")  // tài khoản
                .passwordParameter("j_password") // mật khẩu
                .loginPage("/login.html")
                .failureUrl("/login.html?authentication_error=true") //đường dẫn tới trang đăng nhập thất bại
                .loginProcessingUrl("/login.html") //Trong Spring Security, trang xử lý submit form mặc định là /login. Nếu bạn muốn custom thì có thể dùng loginProcessingUrl().
                .successHandler(userLoginSuccessHandler())
                .permitAll()
//        		.failureHandler(userLoginFailureHandler)
                .and()
                .logout()
                .logoutSuccessUrl("/login.html?logout=true")
                .logoutUrl("/logout.html")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout.html")) // for POST and GET
                .deleteCookies( "JSESSIONID" )
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .sessionManagement()
                .invalidSessionUrl("/login.html?invalid=true") //?invalid
                .maximumSessions(-1) // -1  unlimit
                .expiredUrl("/login.html?expired=true")
                .maxSessionsPreventsLogin(false)// kho cho dang nhap neu da ton tai session
                .and()
                .enableSessionUrlRewriting(false)
        ;
        // @formatter:on
    }
}

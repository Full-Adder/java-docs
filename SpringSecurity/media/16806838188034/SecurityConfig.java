package com.example.day04;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

/**
 * @Author 编程学习
 * @Date 2023/4/8
 * @Description
 */
@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService1(){
        UserDetails userDetails = User.withUsername("memory1").password("{noop}memory1").roles("memory1").build();
        return new InMemoryUserDetailsManager(userDetails);
    }
    @Bean
    public UserDetailsService userDetailsService2(){
        UserDetails userDetails = User.withUsername("memory2").password("{noop}memory2").roles("memory2").build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService1());
        DaoAuthenticationProvider provider2 = new DaoAuthenticationProvider();
        provider2.setUserDetailsService(userDetailsService2());
        return new ProviderManager(provider2,provider);
    }

}

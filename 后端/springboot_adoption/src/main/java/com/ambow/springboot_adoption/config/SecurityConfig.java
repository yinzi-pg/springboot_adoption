package com.ambow.springboot_adoption.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

//配置文件一定要写这个注解
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // 2. 核心：配置 Security 过滤链，放行接口+适配跨域
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ① 关闭 CSRF 保护（前后端分离场景必须关，否则拦截 JSON 请求）
                .csrf(csrf -> csrf.disable())

                // ② 配置跨域（优先级高于之前的全局 CORS，确保 OPTIONS 预检不被拦截）
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ③ 关闭默认的会话管理（前后端分离用 JWT 或自定义登录态，不用 Session）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ④ 配置接口访问规则：放行无需认证的接口，其他接口暂不拦截（后续可加认证）
                .authorizeHttpRequests(auth -> auth
                        // 放行登录、注册接口
                        .requestMatchers("/user/login", "/user/register").permitAll()
                        // 放行图片上传和图片访问接口
                        .requestMatchers("/upload", "/images/**").permitAll()
                        // 放行 OPTIONS 预检请求（所有接口的 OPTIONS 都放行）
                        .requestMatchers(request -> "OPTIONS".equalsIgnoreCase(request.getMethod())).permitAll()
                        // 其他所有接口：暂时放行（后续如果需要认证，改为 .authenticated()）
                        .anyRequest().permitAll()
                )

                // ⑤ 关闭默认的表单登录和注销（不用 Security 的内置登录页）
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

    // 3. 配置 CORS 规则（与 WebConfig 一致，确保 Security 层面也支持跨域）
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的前端域名（与 WebConfig 一致）
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // 允许的请求方法（包含 OPTIONS）
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH","OPTIONS"));
        // 允许的请求头（支持 JSON 的 Content-Type）
        config.setAllowedHeaders(Arrays.asList("*"));
        // 允许携带 Cookie（与 WebConfig 一致）
        config.setAllowCredentials(true);
        // 预检缓存时间
        config.setMaxAge(3600L);

        // 对所有接口生效
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

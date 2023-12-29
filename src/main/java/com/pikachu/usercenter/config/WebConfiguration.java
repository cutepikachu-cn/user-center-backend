package com.pikachu.usercenter.config;

import com.pikachu.usercenter.interceptor.AuthInterceptor;
import com.pikachu.usercenter.interceptor.LoginInterceptor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@SpringBootConfiguration
public class WebConfiguration implements WebMvcConfigurer {
    private final String[] EXCLUDE_PATH_LOGIN = {
            "/user/login",
            "/user/register",
            "/user/search",
            "/team/search",
            "/team/get"
    };
    private final String[] EXCLUDE_PATH_AUTH = {};
    private final String[] CORS_ORIGINS = {
            "http://*.cute-pikachu.cn",
            "https://*.cute-pikachu.cn",
            "http://localhost:[*]"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/user/*", "/admin/*", "/team/*")
                .excludePathPatterns(EXCLUDE_PATH_LOGIN);
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/admin/*")
                .excludePathPatterns(EXCLUDE_PATH_AUTH);
    }

    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     // 设置允许跨域的路径
    //     registry.addMapping("/**")
    //             .allowCredentials(true)
    //             // 设置允许跨域请求的域名
    //             // 当Credentials为true时，Origin不能为星号，需为具体的ip地址【如果接口不带cookie,ip无需设成具体ip】
    //             .allowedOriginPatterns(CORS_ORIGINS)
    //             // 是否允许证书 不再默认开启
    //             // 允许任意响应头
    //             .allowedHeaders("*")
    //             .exposedHeaders("*")
    //             // 设置允许的方法
    //             .allowedMethods("*");
    // }

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowedOriginPatterns(List.of(CORS_ORIGINS));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}

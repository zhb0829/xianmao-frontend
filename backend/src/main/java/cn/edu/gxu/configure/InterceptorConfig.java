package cn.edu.gxu.configure;

import cn.edu.gxu.configure.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.Web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.Web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {//
        registry.addInterceptor(getJwtInterceptor()).addPathPatterns("/**").excludePathPatterns("/user/login","/user/register","/user/getCode"
                ,"/swagger-ui/**","/swagger-resources/**","/v2/**","/api", "/api-docs", "/api-docs/**");
    }

    @Bean
    JwtInterceptor getJwtInterceptor(){
        return  new JwtInterceptor();
    }
}


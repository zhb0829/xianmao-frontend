package cn.edu.gxu.configure;

import org.springframework.boot.Web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.Web.filter.CharacterEncodingFilter;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        // 创建filter登记容器
           FilterRegistrationBean bean = new FilterRegistrationBean();
           //创建字符集过滤器对象
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        //设置编码格式
        characterEncodingFilter.setEncoding("utf-8");
        //强制request需求和过滤器需求
        characterEncodingFilter.setForceEncoding(true);
        //登记过滤器
        bean.setFilter(characterEncodingFilter);
        bean.addUrlPatterns("/*");
        return  bean;
    }
}

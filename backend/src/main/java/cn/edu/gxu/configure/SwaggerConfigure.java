package cn.edu.gxu.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.Web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.Web.plugins.Docket;

import java.util.ArrayList;

@Configuration
@EnableWebMvc
@EnableOpenApi //开启Swagger3
public class SwaggerConfigure {
    //配置Swagger的Docket的bean实例
    @Bean
    public Docket restApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("标准接口")
                .apiInfo(apiInfo("Spring Boot中使用Swagger3.0构建RESTful APIs", "1.0"))
                .useDefaultResponseMessages(true)
                .forCodeGeneration(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.edu.gxu.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    //配置Swagger信息
    /*String title, String description,
     String version, String termsOfServiceUrl,
     Contact contact, String license,
     String licenseUrl,
     Collection<VendorExtension> vendorExtensions*/
    private ApiInfo apiInfo(String title,String version) {
        return new ApiInfoBuilder().title(title).version(version).description("Web课设").build();


    }
}

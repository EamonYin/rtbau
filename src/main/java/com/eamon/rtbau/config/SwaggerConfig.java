package com.eamon.rtbau.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eamon
 * @desc Swagger配置类，该类里面的应该是固定的，主要用来设置文档的主题信息，比如文档的大标题，副标题，公司名
 * 等
 */
@Configuration//托管spring
@EnableSwagger2//开启swagger功能
public class SwaggerConfig {
    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描的包路径来定义指定要建立API的目录。
     *
     * @return
     */
    @Bean
    public Docket createRestApi(){
        //版本类型是swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                //通过调用自定义方法apiInfo，获得文档的主要信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.eamon.paypal.modules"))//扫描该包下面的API注解
                .paths(PathSelectors.any())
                .build()
//                //全局参数
//                .globalOperationParameters(getGlobalOperationParameters())
                //全站统一参数token
                .securitySchemes(authToken())
        ;
    }
    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("使用Swagger2 构建RESTful APIS - Test") //接口管理文档首页显示
                .description("Test - Swagger使用接口演示 Header 携带语言ZH 或者EN")//API的描述
                .termsOfServiceUrl("localhost:8090/practice-user/")//网站url等
                .version("application-dev.yml.0")
                .build();
    }
    //全站统一参数配置，一般是token。
    private  List<ApiKey> authToken() {
        List<ApiKey> arrayList = new ArrayList();
        arrayList.add(new ApiKey("stu-token", "stu-token", "header"));
        arrayList.add(new ApiKey("stu-language", "stu-language", "header"));

        return arrayList;
    }
    //全局参数配置
    private List<Parameter> getGlobalOperationParameters() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        // header query cookie
        parameterBuilder.name("stu-token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false);
        pars.add(parameterBuilder.build());
        parameterBuilder.name("stu-language").description("语言").modelRef(new ModelRef("string")).parameterType("header").required(false);
        pars.add(parameterBuilder.build());
        return pars;
    }
}
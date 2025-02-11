package jsbh.Jusangbokhap.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("결제 API 문서")
                        .version("v1")
                        .description("숙소 예약 시스템의 결제 API 문서"));
    }

    @Bean
    public OperationCustomizer customizeOperation() {
        return (operation, handlerMethod) -> {
            Class<?> declaringClass = handlerMethod.getMethod().getDeclaringClass();
            if (declaringClass.getPackageName().startsWith("jsbh.Jusangbokhap.domain")) {
                operation.setDeprecated(true);
            }
            return operation;
        };
    }
}

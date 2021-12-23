package com.skillbox.javapro21.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * на данный момент свагер запускается по этой ссылке -> http://localhost:8080/swagger-ui.html
 */

@Configuration
@OpenAPIDefinition(info = @Info(title = "Group diploma by Skillbox Java-PRO course", version = "0.0.1 SNAPSHOT"))
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "jwt", paramName = "Authorization", in = SecuritySchemeIn.HEADER, scheme = "Bearer")
public class SpringFoxConfig {
}

package kamaz.project.sandbox.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer{
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("file:///" + uploadPath + "/");
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(new StringToLocalDateConverter());
    }

    private static class StringToLocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(@NonNull String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(source);
        }
    }

}

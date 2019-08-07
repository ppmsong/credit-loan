package isec.loan.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 定义文件访问路径
 *
 * @author p
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

	@Autowired
	private InParamInjecter inParamInjecter;

	@Autowired
	private ControllerAspect controllerAspect;

	@Value("${uploadDir}")
	private String uploadDir;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		super.addArgumentResolvers(argumentResolvers);
		argumentResolvers.add(inParamInjecter);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(controllerAspect).addPathPatterns("/**");
	}

	/**
	 * 配置静态访问资源
	 *
	 * @param registry http://localhost:8080/upload/2018/9/6d9319ae244345c3ae0983320eb9cb5a.jpg
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 第一个参数 访问前缀
		// 映射的目录
		registry.addResourceHandler("/ws-lx/upload/**").addResourceLocations("file:" + uploadDir + "/");
		super.addResourceHandlers(registry);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
 
}

package isec.loan.common;

import isec.loan.core.PromptException;
import isec.loan.core.StatusCodeEnum;
import isec.loan.entity.OperLogon;
import isec.loan.entity.User;
import isec.loan.service.OperLogonService;
import isec.loan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

@Component
public class InParamInjecter implements HandlerMethodArgumentResolver {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;
	@Autowired
	private OperLogonService operLogonService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(In.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		// 注入member对象
		boolean isRequired=parameter.getParameterAnnotation(In.class).required();
		if (parameter.getParameterType().equals(User.class)) {
			if(!parameterMap.containsKey("token")&&!isRequired) {
				return null;
			}
			if (!parameterMap.containsKey("token")) {
				throw new PromptException(StatusCodeEnum.TOKEN_NOTEMPTY);
			}
			String tokenValue = parameterMap.get("token")[0];
			OperLogon operLogon = operLogonService.getOperLogon(tokenValue);
			if (operLogon == null) {
				throw new PromptException(StatusCodeEnum.TOKEN_INVALID);
			}

			//更新在线用户
			operLogonService.updateOperLogon(tokenValue);

			User user = userService.findById(operLogon.getOperId());
//			user.setToken(tokenValue);
			return user;
		}
		return null;

	}

}

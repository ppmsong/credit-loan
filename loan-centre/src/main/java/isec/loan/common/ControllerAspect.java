package isec.loan.common;

import com.alibaba.fastjson.JSONObject;
import isec.base.util.S;
import isec.loan.configurer.Config;
import isec.loan.core.PromptException;
import isec.loan.core.StatusCode;
import isec.loan.core.StatusCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.UnsupportedEncodingException;
import java.util.*;

//@Aspect
@Component
@ControllerAdvice(basePackages = "isec.loan.controller")
public class ControllerAspect implements ResponseBodyAdvice<Object>, HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Config config;


//	@Around("execution(public * isec.card.controller.*.*(..))&&@annotation(org.springframework.web.bind.annotation.RequestMapping)")
//	public Object execute(ProceedingJoinPoint pjp) throws Throwable {
//		Object o = pjp.proceed();
//		if (o instanceof ModelMap) {
//			Map returnMap = new HashMap();
//			ModelMap modelMap = (ModelMap) o;
//			modelMap.addAllAttributes(returnMap);
//			return returnMap;
//		}
//		return o;
//	}

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        Map map = new HashMap();
        map.put("data", body);
        setStatus(new StatusCode(StatusCodeEnum.SUCCESS.getCode(), StatusCodeEnum.SUCCESS.getMessage()), map);
        if (String.class.equals(returnType.getParameterType())) {
            return JSONObject.toJSONString(map);
        }
        logger.debug("请求结果=>" + JSONObject.toJSONString(map));
        return map;
    }

    private void setStatus(StatusCode statusCode, Map map) {
        if (map != null) {
            map.put("code", statusCode.getCode());
            map.put("msg", statusCode.getMessage());
        }
    }

    @ResponseBody
    @ExceptionHandler(value = PromptException.class)
    public Object promptExceptionHandler(PromptException e) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        setStatus(e.getStatusCode(), map);
        logger.warn("controller prompt {}", e.getMessage());
        return map;
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object globalExceptionHandler(Exception e) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        setStatus(new StatusCode(StatusCodeEnum.FAIL.getCode(), StatusCodeEnum.FAIL.getMessage()), map);
        logger.error("controller execute发生错误", e);
        return map;
    }

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Object constraintViolationException(ConstraintViolationException e) throws Exception {
        Map map = new HashMap();
        ConstraintViolation constraintViolation = e.getConstraintViolations().iterator().next();
        setStatus(new StatusCode(StatusCodeEnum.PARAM_VALIDATE_FAIL.getCode(), constraintViolation.getMessage()), map);
        logger.warn("controller violation fail {} {}", constraintViolation.getPropertyPath(), constraintViolation.getMessage());
        return map;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String equip = request.getHeader("equip");
        String qd = request.getHeader("qd");
        logger.debug(
                "请求信息==>  地址:{}   参数:{} "
                        + " equip:{}  qd:{} signOnOff=", request.getRequestURI(), JSONObject.toJSONString(request.getParameterMap()),equip, qd, config.getSignOnOff());

        if ("off".equals(config.getSignOnOff()) || !("android".equals(equip) || "ios".equals(equip))) {
            logger.debug("无需签名认证");
            return true;
        }
        // 签名验证
        String sign = request.getParameter("sign");
        if (S.isBlank(sign)) {
            throw new PromptException("无sign参数");
        }

        String generateSign = Sign.getSign(getSignParam(request), config.getSignKey());
        if (!sign.equals(generateSign)) {
            throw new PromptException("签名错误");
        }
        return true;
    }

    private SortedMap<String, Object> getSignParam(HttpServletRequest request) throws UnsupportedEncodingException {
        SortedMap<String, Object> signParam = new TreeMap<String, Object>();
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paramName = enu.nextElement();
            if (!paramName.equals("sign")) {
                signParam.put(paramName, request.getParameter(paramName));
            }
        }
        return signParam;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }

}

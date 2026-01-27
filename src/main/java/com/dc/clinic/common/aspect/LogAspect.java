package com.dc.clinic.common.aspect;

import com.dc.clinic.common.annotation.Log;
import com.dc.clinic.modules.auth.dto.LoginUser;
import com.dc.clinic.modules.system.entity.OperationLog;
import com.dc.clinic.modules.system.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private OperationLogService logService;

    @Autowired
    private ObjectMapper objectMapper;

    // 定义切点
    @Pointcut("@annotation(com.dc.clinic.common.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 成功返回后的拦截
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 抛出异常后的拦截
     */
    @AfterThrowing(pointcut = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            // 获取方法上的 @Log 注解
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            // 创建日志对象
            OperationLog operLog = new OperationLog();
            operLog.setStatus(0); // 默认正常

            // 1. 获取当前登录人
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                    LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                    operLog.setOperatorName(loginUser.getUsername());
                }
            } catch (Exception ex) {
                logger.warn("获取登录用户信息失败", ex);
            }

            // 2. 获取请求信息
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperIp(getIpAddress(request));
            operLog.setRequestMethod(request.getMethod());
            operLog.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

            // 3. 注解上的参数
            operLog.setTitle(controllerLog.title());
            operLog.setBusinessType(controllerLog.businessType());

            // 4. 记录请求参数
            try {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    String params = objectMapper.writeValueAsString(args);
                    // 截断过长的参数（避免数据库字段溢出）
                    if (params.length() > 5000) {
                        params = params.substring(0, 5000);
                    }
                    operLog.setOperParam(params);
                }
            } catch (Exception ex) {
                logger.warn("记录请求参数失败", ex);
            }

            // 5. 记录返回结果
            if (jsonResult != null) {
                try {
                    String result = objectMapper.writeValueAsString(jsonResult);
                    if (result.length() > 5000) {
                        result = result.substring(0, 5000);
                    }
                    operLog.setJsonResult(result);
                } catch (Exception ex) {
                    logger.warn("记录返回结果失败", ex);
                }
            }

            // 6. 处理异常
            if (e != null) {
                operLog.setStatus(1);
                operLog.setErrorMsg(e.getMessage());
            }

            // 7. 设置操作时间
            operLog.setOperTime(new Date());

            // 8. 保存到数据库（建议异步保存）
            logService.save(operLog);
            logger.debug("操作日志记录成功: {}", operLog.getTitle());

        } catch (Exception ex) {
            logger.error("记录操作日志异常", ex);
        }
    }

    /**
     * 获取注解信息
     */
    private Log getAnnotationLog(JoinPoint joinPoint) {
        try {
            // 获取方法签名
            org.aspectj.lang.Signature signature = joinPoint.getSignature();
            if (!(signature instanceof org.aspectj.lang.reflect.MethodSignature)) {
                return null;
            }

            org.aspectj.lang.reflect.MethodSignature methodSignature = (org.aspectj.lang.reflect.MethodSignature) signature;
            // 获取方法上的注解
            return methodSignature.getMethod().getAnnotation(Log.class);
        } catch (Exception e) {
            logger.error("获取Log注解失败", e);
            return null;
        }
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个IP时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
package com.dental.clinic.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.UUID;

/**
 * 安全的 Web 请求日志切面（生产环境可用）
 * - 自动过滤不可序列化/敏感参数
 * - 支持配置开关控制日志级别
 * - 集成 MDC traceId 便于日志追踪
 */
@Aspect
@Component
public class SafeWebLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(SafeWebLogAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 配置项：是否记录请求参数（默认 false，生产建议关闭）
    @Value("${app.logging.log-request-args:false}")
    private boolean logRequestArgs;

    // 配置项：是否记录响应结果（默认 false，生产建议关闭）
    @Value("${app.logging.log-response:false}")
    private boolean logResponse;

    // 切点：所有 controller 方法（排除某些特殊路径可在此扩展）
    @Pointcut("execution(public * com.dental.clinic.controller..*(..))")
    public void webLog() {}

    /**
     * 前置通知：记录请求基本信息 + 安全参数
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 生成并设置 traceId（可用于全链路追踪）
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        MDC.put("traceId", traceId);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("======= Web Log Start [{}] =======", traceId);
        logger.info("URL: {}", request.getRequestURL());
        logger.info("HTTP Method: {}", request.getMethod());
        logger.info("Class.Method: {}.{}", className, methodName);
        logger.info("IP: {}", getRealIp(request));

        if (logRequestArgs) {
            String safeArgs = safeSerialize(joinPoint.getArgs());
            logger.info("Request Args: {}", safeArgs);
        }
    }

    /**
     * 后置返回通知：记录响应（可选）
     */
    @AfterReturning(returning = "result", pointcut = "webLog()")
    public void doAfterReturning(Object result) {
        if (logResponse) {
            String safeResult = safeSerialize(result);
            logger.info("Response: {}", safeResult);
        }
        logger.info("===================== Web Log End [{}] =====================", MDC.get("traceId"));
        MDC.clear(); // 清理 MDC
    }

    /**
     * 异常通知：记录异常但不中断
     */
    @AfterThrowing(pointcut = "webLog()", throwing = "ex")
    public void doAfterThrowing(Exception ex) {
        logger.error("Exception occurred: {}", ex.getMessage(), ex);
        logger.info("===================== Web Log End [{}] (with exception) =====================", MDC.get("traceId"));
        MDC.clear();
    }

    /**
     * 安全序列化任意对象，自动过滤敏感类型
     */
    private String safeSerialize(Object obj) {
        try {
            if (obj == null) {
                return "null";
            }

            // 如果是数组，逐个处理
            if (obj instanceof Object[]) {
                Object[] arr = (Object[]) obj;
                Object[] filtered = Arrays.stream(arr)
                        .map(this::filterSensitive)
                        .toArray();
                return objectMapper.writeValueAsString(filtered);
            } else {
                Object filtered = filterSensitive(obj);
                return objectMapper.writeValueAsString(filtered);
            }
        } catch (JsonProcessingException e) {
            return "[SERIALIZATION ERROR: " + e.getMessage() + "]";
        } catch (Exception e) {
            return "[UNEXPECTED ERROR IN SERIALIZATION]";
        }
    }

    /**
     * 过滤敏感或不可序列化的对象
     */
    private Object filterSensitive(Object arg) {
        if (arg == null) {
            return null;
        }

        Class<?> clazz = arg.getClass();

        // 过滤 Servlet API 和 Spring Web 相关对象
        if (arg instanceof HttpServletRequest ||
                arg instanceof HttpServletResponse ||
                arg instanceof jakarta.servlet.ServletRequest ||
                arg instanceof jakarta.servlet.ServletResponse ||
                arg instanceof MultipartFile ||
                clazz.getName().startsWith("org.springframework.web.") ||
                clazz.getName().startsWith("jakarta.servlet.") ||
                clazz.getName().startsWith("org.apache.catalina.") ||
                clazz.getName().startsWith("org.eclipse.jetty.")) {

            return "[Filtered: " + clazz.getSimpleName() + "]";
        }

        // 可选：过滤包含敏感字段的对象（如密码），可根据需要扩展
        return arg;
    }

    /**
     * 获取真实客户端 IP（支持代理）
     */
    private String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}
package com.dc.clinic.common.aspect;

import com.alibaba.fastjson2.JSON; // 建议引入 fastjson2 或使用 Jackson
import com.dc.clinic.common.annotation.Log;
import com.dc.clinic.modules.auth.dto.LoginUser;
import com.dc.clinic.modules.system.entity.OperationLog;
import com.dc.clinic.modules.system.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperationLogService logService;

    /**
     * 成功返回后的拦截
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 抛出异常后的拦截
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            OperationLog operLog = new OperationLog();
            operLog.setStatus(0); // 默认正常
            
            // 1. 获取当前登录人 (从 SecurityContext 拿)
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof LoginUser) {
                operLog.setOperatorName(((LoginUser) principal).getUsername());
            }

            // 2. 获取请求信息
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperIp(request.getRemoteAddr());
            operLog.setRequestMethod(request.getMethod());
            operLog.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            
            // 3. 注解上的参数
            operLog.setTitle(controllerLog.title());
            operLog.setBusinessType(controllerLog.businessType());

            // 4. 处理错误
            if (e != null) {
                operLog.setStatus(1);
                operLog.setErrorMsg(e.getMessage());
            }
            
            // 5. 保存到数据库 (异步保存更佳，这里先同步)
            logService.save(operLog);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
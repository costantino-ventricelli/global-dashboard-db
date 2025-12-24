package com.globaldashboard.db.aop;

import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.messaging.annotation.MessageHeader;
import jakarta.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.slf4j.MDC;


@Singleton
@InterceptorBean(KafkaMdc.class)
public class KafkaMdcInterceptor implements MethodInterceptor<Object, Object> {

    @Nullable
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        // Find argument with @MessageHeader or just Map<String, byte[]> which we assume are headers
        // if using the specific signature of our consumers

        Map<String, byte[]> headers = findHeaders(context);
        if (headers != null) {
            headers.forEach((k, v) -> {
                if (v != null) {
                    MDC.put(k, new String(v, StandardCharsets.UTF_8));
                }
            });
        }

        try {
            return context.proceed();
        } finally {
            MDC.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, byte[]> findHeaders(MethodInvocationContext<Object, Object> context) {
        Object[] values = context.getParameterValues();
        for (int i = 0; i < values.length; i++) {
            // Check for @MessageHeader annotation on the parameter
            // Note: Micronaut interceptors might make parameter annotation inspection tricky if
            // proxies are involved,
            // but checking value type is a robust fallback for this specific use case.
            if (values[i] instanceof Map) {
                // We assume the Map<String, byte[]> in our listener is the headers
                try {
                    return (Map<String, byte[]>) values[i];
                } catch (ClassCastException ignored) {
                    // Not the map we are looking for
                }
            }
        }
        return null;
    }
}

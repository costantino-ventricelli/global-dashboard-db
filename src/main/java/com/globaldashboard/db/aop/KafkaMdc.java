package com.globaldashboard.db.aop;

import io.micronaut.aop.Around;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to automatically populate MDC from Kafka Headers. Put this on @KafkaListener methods.
 */
@Around
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface KafkaMdc {
}

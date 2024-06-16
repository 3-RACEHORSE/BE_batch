package com.meetplus.batch.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @PersistenceContext(unitName = "batchEntityManager")
    private EntityManager batchEntityManager;

    @PersistenceContext(unitName = "paymentEntityManager")
    private EntityManager paymentEntityManager;
}

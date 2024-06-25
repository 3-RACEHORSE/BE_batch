package com.meetplus.batch.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

    @Bean
    public JPAQueryFactory batchJpaQueryFactory(@Qualifier("batchEntityManagerFactory") EntityManagerFactory batchEntityManagerFactory) {
        EntityManager batchEntityManager = batchEntityManagerFactory.createEntityManager();
        return new JPAQueryFactory(batchEntityManager);
    }

    @Bean
    public JPAQueryFactory paymentJpaQueryFactory(@Qualifier("paymentEntityManagerFactory") EntityManagerFactory paymentEntityManagerFactory) {
        EntityManager paymentEntityManager = paymentEntityManagerFactory.createEntityManager();
        return new JPAQueryFactory(paymentEntityManager);
    }

    @Bean
    public JPAQueryFactory BankJpaQueryFactory(@Qualifier("bankEntityManagerFactory") EntityManagerFactory bankEntityManagerFactory) {
        EntityManager bankEntityManager = bankEntityManagerFactory.createEntityManager();
        return new JPAQueryFactory(bankEntityManager);
    }
}

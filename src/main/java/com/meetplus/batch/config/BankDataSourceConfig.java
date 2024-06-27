//package com.meetplus.batch.config;
//
//import jakarta.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//    basePackages = "com.meetplus.batch.infrastructure.bank",
//    entityManagerFactoryRef = "bankEntityManagerFactory",
//    transactionManagerRef = "bankTransactionManager"
//)
//public class BankDataSourceConfig {
//
//    @Bean(name = "bankDataSource")
//    @ConfigurationProperties(prefix = "spring.bank.datasource")
//    public DataSource bankDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "bankEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean bankEntityManagerFactory(
//        EntityManagerFactoryBuilder builder,
//        @Qualifier("bankDataSource") DataSource bankDataSource
//    ) {
//        return builder
//            .dataSource(bankDataSource)
//            .packages("com.meetplus.batch.domain.bank")
//            .build();
//    }
//
//    @Bean(name = "bankTransactionManager")
//    public PlatformTransactionManager bankTransactionManager(
//        @Qualifier("bankEntityManagerFactory") EntityManagerFactory bankEntityManagerFactory) {
//        return new JpaTransactionManager(bankEntityManagerFactory);
//    }
//}
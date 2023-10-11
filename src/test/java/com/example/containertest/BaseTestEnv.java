package com.example.containertest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BaseTestEnv extends MRContainer {

//    @Getter
//    private String redisHost;
//    @Getter
//    private Integer redisPort;
//
//    @Getter
//    private String mysqlUrl;

    boolean runRedisContainer = false;
    boolean runMySqlContainer = false;


    public BaseTestEnv() {
        boolean runWithContainer = Boolean.valueOf(System.getProperty("testcontainers.container", "true"));
        runRedisContainer = Boolean.valueOf(System.getProperty("testcontainers.redis", "false"));
        runMySqlContainer = Boolean.valueOf(System.getProperty("testcontainers.mysql", "false"));
        if(runWithContainer){
            runRedisContainer = true;
            runMySqlContainer = true;
        }

        log.info("===========================initing BaseTestEnv========================");

        setup0(runRedisContainer,runMySqlContainer);

        if(runRedisContainer){
            log.info("redis.contaer started.IP and Port:{}|{}",redis.getHost(),redis.getFirstMappedPort().toString());
            System.setProperty("spring.redis.host",redis.getHost());
            System.setProperty("spring.redis.port",redis.getFirstMappedPort().toString());
        }
        if(runMySqlContainer){
            log.info("mysql.container:{}|{}",mysql.getHost(),mysql.getFirstMappedPort());
            String mysqlUrl = "jdbc:mysql://"+mysql.getHost()+":"+mysql.getFirstMappedPort()+"/test?useSSL=false";//Testcontainers库会自动创建一个默认数据库test
            System.setProperty("spring.datasource.druid.url",mysqlUrl);
            System.setProperty("spring.datasource.druid.username","root");
            System.setProperty("spring.datasource.druid.password","test");
        }
    }

    public BaseTestEnv(boolean initRedisContainer, boolean initMysqlContainer){
        super(initRedisContainer,initMysqlContainer);
    }
}

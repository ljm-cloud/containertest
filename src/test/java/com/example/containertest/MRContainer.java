package com.example.containertest;

import com.github.dockerjava.api.command.CommitCmd;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * mysql and redis container
 */
@Slf4j
public class MRContainer {

    private static final ReentrantLock reentrantLock = new ReentrantLock();
    private static String withDataMysqlImageName = null;

    public MRContainer(){
    }

    public MRContainer(boolean initRedisContainer, boolean initMysqlContainer){
//        super(initRedisContainer);
        // ENV
        // DOCKER_HOST  tcp://{IP}:2375
        // TESTCONTAINERS_HOST_OVERRIDE {IP}

        // UUID.randomUUID().toString()
//        String tempPath = JoinerUtil.joiner("/",true,"/data/","test", TimeUtil.getTime().getYMD());
//        File f = new File(tempPath);
//        boolean persistentVolumn = true;


    }

    private MySQLContainer creatMysqlContainer(String fullImageName){
        return (MySQLContainer) new MySQLContainer(DockerImageName.parse(fullImageName).asCompatibleSubstituteFor("mysql"))
                .withExposedPorts(3306)
                .withEnv("MYSQL_ROOT_PASSWORD","test")
                .withCommand("--character-set-server=utf8 --collation-server=utf8_unicode_ci");
    }

    public MySQLContainer mysql ;

    public GenericContainer<?> redis ;

    public void setup0(boolean initRedisContainer,boolean initMysqlContainer){

        List<CompletableFuture<Void>> cfs = Lists.newArrayList();

        if(initRedisContainer) {
            log.info("redis container starting...");
            cfs.add(CompletableFuture.runAsync(()->{
                redis= new GenericContainer<>(DockerImageName.parse("redis:7.2.0")).withExposedPorts(6379);
                redis.start();
                log.info("redis container started,id:{}",redis.getContainerId());
            }));
        }



        if(initMysqlContainer) {
            log.info("mysql container starting...");

            boolean mysqlCustomImageLock = Boolean.valueOf(System.getProperty("testcontainers.mysql.customimage.lock", "false"));

            CompletableFuture<Void> mysqlCF = CompletableFuture.runAsync(() -> {
                if (mysqlCustomImageLock && reentrantLock.tryLock() && withDataMysqlImageName == null) {
                    try {
                        //制作包含基础数据的mysql镜像
                        GenericContainer container = new GenericContainer(new ImageFromDockerfile()
                                .withFileFromClasspath("Dockerfile", "Dockerfile")
                                .withFileFromClasspath("mysqld.cnf", "mysqld.cnf")
                                .withFileFromClasspath("struct.sql", "struct.sql"));
                        container.start();

//            执行docker commit 命令制作一个含有数据且不需要初始化sql的镜像
                        CommitCmd commitCmd = container.getDockerClient()
                                .commitCmd(container.getContainerName())
                                .withRepository("mysql")
                                .withTag("test-5.7.34");
                        String withDataMysqlImageId = commitCmd.exec();//得到含有数据且不需要初始化sql的镜像的id
                        withDataMysqlImageName = commitCmd.getRepository() + ":" + commitCmd.getTag();
                        container.stop();
                        log.info("MRContainer.Create WithDataMysqlImage success|ImageId:{}|ImageName:{}", withDataMysqlImageId, withDataMysqlImageName);
                    } catch (Exception e) {
                        log.error("MRContiner.Create WithDataMysqlImage fail", e);
                    } finally {
                        reentrantLock.unlock();
                    }
                }
                int timeoutSec = Integer.parseInt(System.getProperty("testcontainers.mysql.customimage.timeout", "30"));
                LocalDateTime startWait = LocalDateTime.now();
                LocalDateTime now;
                log.info("Start wait CreateWithDataMysqlImage");
                while (true) {
                    if(!mysqlCustomImageLock){
                        break;
                    }

                    if (withDataMysqlImageName != null) {
                        // 使用这个新镜像
                        mysql = creatMysqlContainer(withDataMysqlImageName);
                        log.info("creatMysqlContainer withDataMysqlImage success");
                        break;
                    } else {
                        now = LocalDateTime.now();
                        if (Duration.between(startWait, now).getSeconds() > timeoutSec) {
                            break;
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        log.error("MRContiner.Thread.sleep error", e);
                    }
                }

                if(mysql==null){
                    //如果超时时间到了还没制作好基础镜像则使用加载初始化sql方式跑 mysql 镜像
                    mysql = creatMysqlContainer("mysql:5.7.34");
                    mysql.withInitScript("struct.sql");
                    log.info("timeout.CreateWithDataMysqlImage");
                }

                log.info("end wait CreateWithDataMysqlImage");
            }).thenAccept(__ -> {
                mysql.start();
                log.info("mysql container started,id:{}", mysql.getContainerId());
            });
            cfs.add(mysqlCF);
        }

        cfs.forEach(CompletableFuture::join);
    }

    public void close0(boolean initRedisContainer,boolean initMysqlContainer){
        List<CompletableFuture<Void>> cfs = Lists.newArrayList();
        if(initRedisContainer) {
            cfs.add(CompletableFuture.runAsync(()->{
                redis.close();
            }));
        }
        if(initMysqlContainer) {
            cfs.add(CompletableFuture.runAsync(()->{
                mysql.close();
            }));
        }
        cfs.forEach(CompletableFuture::join);
    }

}

package com.example.containertest;

import com.example.containertest.entity.Config;
import com.example.containertest.mapper.ConfigMapper;
import com.example.containertest.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Date;
import java.util.List;

@SpringBootTest(classes = ContainertestApplicationEnv.class)
@Slf4j
class ContainertestApplicationTests {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private ConfigMapper configMapper;

	@Test
	void contextLoads() {
		stringRedisTemplate.opsForValue().set("1","1");
		log.info("ContainertestApplicationTests.contextLoads.redis.get|{}",stringRedisTemplate.opsForValue().get("1"));

		Config config = new Config();
		config.setConfKey("key");
		config.setConfValue("value");
		config.setCreateTime(new Date());
		config.setUpdateTime(new Date());
		config.setConfRemark("remark");
		configMapper.insert(config);
		List<Config> configs = configMapper.selectAll();
		log.info("ContainertestApplicationTests.contextLoads.mysql.get|{}", JSONUtils.beanToJson(configs));
	}

}

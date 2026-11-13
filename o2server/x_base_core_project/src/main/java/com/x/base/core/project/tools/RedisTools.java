package com.x.base.core.project.tools;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Cache.Redis;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTools {
	private static final Logger log = LoggerFactory.getLogger(RedisTools.class);
	private static ReentrantLock lockPool = new ReentrantLock();

	private static RedisTools instance;

	private static JedisPool jedisPool;

	private static ReentrantLock lock = new ReentrantLock();

	private RedisTools() {
	}

	public static RedisTools getInstance() {
		if (instance == null) {
			lock.lock();
			instance = new RedisTools();
			lock.unlock();
		}
		return instance;
	}

	/**
	 * JedisPool 초기화
	 */
	private static void initJedisPool() throws Exception {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 최대 인스턴스 총수 설정
		jedisPoolConfig.setMaxTotal(300);
		// pool 하나에 idle(유휴) 상태 jedis 인스턴스를 최대 몇 개 둘지 제어.
		jedisPoolConfig.setMaxIdle(5);
		jedisPoolConfig.setMinIdle(5);
		// jedis 인스턴스를 borrow(대여)할 때 최대 대기 시간, 대기 시간을 넘으면 바로 JedisConnectionException 발생;
		jedisPoolConfig.setMaxWaitMillis(3 * 1000);
		// jedis 인스턴스를 borrow할 때 미리 validate 수행 여부; true면 얻은 jedis 인스턴스는 모두 사용 가능;
		jedisPoolConfig.setTestOnBorrow(true);
		// pool에 반환할 때 미리 validate 수행 여부
		jedisPoolConfig.setTestOnReturn(true);
		jedisPoolConfig.setTestWhileIdle(true);
		jedisPoolConfig.setMinEvictableIdleTimeMillis(500);
		jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(1000);
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(1000);
		jedisPoolConfig.setNumTestsPerEvictionRun(100);
		Redis redis = Config.cache().getRedis();
		String user = redis.getUser();
		String password = redis.getPassword();
		if (StringUtils.isBlank(redis.getUser())) {
			user = null;
		}
		if (StringUtils.isBlank(redis.getPassword())) {
			password = null;
		}
		jedisPool = new JedisPool(jedisPoolConfig, redis.getHost(), redis.getPort(), redis.getSocketTimeout(), user,
				password, redis.getIndex(), redis.getSslEnable());
	}

	/**
	 * 공통 메서드: JedisPool에서 Jedis 가져오기
	 *
	 * @return
	 */
	public static Jedis getJedis() {
		Jedis resource = null;
		lockPool.lock();
		try {
			if (jedisPool == null) {
				initJedisPool();
				log.debug("JedisPool init success！");
			}
			if (jedisPool != null) {
				resource = jedisPool.getResource();
			}
		} catch (Exception e) {
			log.warn("JedisPool init error: {}", e.getMessage());
		}
		lockPool.unlock();
		if (resource == null) {
			log.warn("get redis connect from redisPool is null");
		}
		return resource;
	}

	/**
	 * 공통 메서드: Jedis 해제
	 *
	 * @param jedis
	 */
	public static void closeJedis(Jedis jedis) {
		try {
			if (jedis != null) {
				jedis.close();
			}
		} catch (Exception e) {
		}
	}

	public static void closePool() {
		try {
			if (jedisPool != null) {
				jedisPool.destroy();
			}
		} catch (Exception e) {
		}
	}

}

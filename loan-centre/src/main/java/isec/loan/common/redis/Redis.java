package isec.loan.common.redis;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Repository
public class Redis<T> {

	private Logger logger = LoggerFactory.getLogger(Redis.class);

	@Autowired
	private JedisPool jedisPool;

	/**
	 * 保存对象
	 * 
	 * @param key
	 *            键
	 * @param o
	 *            对象
	 * @param seconds
	 *            过期时间（秒）
	 */
	public void setObject(String key, T o, int seconds) {
		if (o != null) {
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.set(key, JSON.toJSONString(o));
				if (seconds > 0) {
					jedis.expire(key, seconds);
				}

			} catch (Exception e) {
				logger.error("redis异常：设置" + key, e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
	}

	/**
	 * 获取对象
	 * 
	 * @param key
	 *            键
	 * @param clazz
	 *            对象类型
	 * @return 对象
	 */
	public T getObject(String key, Class<T> clazz) {
		T o = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			if (jedis.exists(key)) {
				o = JSON.parseObject(jedis.get(key), clazz);
			}
		} catch (Exception e) {
			logger.error("redis异常：读取" + key, e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return o;
	}

	/**
	 * 保存列表
	 * 
	 * @param key
	 *            键
	 * @param list
	 *            列表
	 */
	public void setList(String key, List<T> list) {
		if (list != null && !list.isEmpty()) {
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.set(key, JSON.toJSONString(list));

			} catch (Exception e) {
				logger.error("redis异常：设置" + key, e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
	}

	/**
	 * 获取列表
	 * 
	 * @param key
	 *            键
	 * @param clazz
	 *            列表中的对象类型
	 * @return 列表
	 */
	public List<T> getList(String key, Class<T> clazz) {
		List<T> list = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			if (jedis.exists(key)) {
				list = JSON.parseArray(jedis.get(key), clazz);
			}
		} catch (Exception e) {
			logger.error("redis异常：读取" + key, e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return list;
	}

	/**
	 * 删除缓存
	 * 
	 * @param keys
	 *            键
	 */
	public void del(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(keys);

		} catch (Exception e) {
			logger.error("redis异常：删除" + keys, e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 删除前缀为preStr的所有key
	 * 
	 * @param preStr
	 *            前缀
	 */
	public void batchDel(String preStr) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> set = jedis.keys(preStr + "*");
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String keyStr = it.next();
				jedis.del(keyStr);
			}
		} catch (Exception e) {
			logger.error("redis异常：删除前缀" + preStr, e);
		}finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		
	}

	/**
	 * 删除缓存(所有)
	 * 

	 */
	public void flushDB() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.flushDB();

		} catch (Exception e) {
			logger.error("redis异常：删除所有", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 获取键集
	 * 
	 * @param pattern
	 *            模式
	 * @return 键集
	 */
	public String[] keys(String pattern) {
		String[] keys = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			Set<String> set = jedis.keys(pattern);
			if (set != null && !set.isEmpty()) {
				keys = new String[set.size()];
				set.toArray(keys);
			}

		} catch (Exception e) {
			logger.error("redis异常：获取" + pattern + "键集", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return keys;
	}

	
	public boolean isExist(String key, int seconds) {
		if (null == getObject(key, (Class<T>) Object.class)) {
			 setObject(key, (T) key, seconds);
			return false;
		}
		return true;
	}



}

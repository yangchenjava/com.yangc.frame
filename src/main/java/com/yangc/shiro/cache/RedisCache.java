package com.yangc.shiro.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import redis.clients.jedis.ShardedJedis;

import com.yangc.utils.cache.RedisUtils;
import com.yangc.utils.io.SerializeUtils;

public class RedisCache<K, V> implements Cache<K, V> {

	private byte[] cacheName;

	public RedisCache(String cacheName) {
		this.cacheName = cacheName.getBytes();
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(K key) throws CacheException {
		if (key != null) {
			RedisUtils cache = RedisUtils.getInstance();
			ShardedJedis jedis = null;
			try {
				jedis = cache.getJedis();
				return (V) SerializeUtils.deserialize(jedis.hget(this.cacheName, SerializeUtils.serialize(key)));
			} catch (Exception e) {
				e.printStackTrace();
				cache.returnBrokenResource(jedis);
				throw new CacheException();
			} finally {
				cache.returnResource(jedis);
			}
		}
		return null;
	}

	@Override
	public V put(K key, V value) throws CacheException {
		if (key != null && value != null) {
			RedisUtils cache = RedisUtils.getInstance();
			ShardedJedis jedis = null;
			try {
				jedis = cache.getJedis();
				jedis.hset(this.cacheName, SerializeUtils.serialize(key), SerializeUtils.serialize(value));
				return value;
			} catch (Exception e) {
				e.printStackTrace();
				cache.returnBrokenResource(jedis);
				throw new CacheException();
			} finally {
				cache.returnResource(jedis);
			}
		}
		return null;
	}

	@Override
	public V remove(K key) throws CacheException {
		return null;
	}

	@Override
	public void clear() throws CacheException {

	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Set<K> keys() {
		return null;
	}

	@Override
	public Collection<V> values() {
		return null;
	}

}

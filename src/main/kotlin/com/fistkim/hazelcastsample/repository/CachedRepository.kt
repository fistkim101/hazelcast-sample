package com.fistkim.hazelcastsample.repository

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CachedRepository<K, E> {

    val logger: Logger
        get() = LoggerFactory.getLogger(CachedRepository::class.java)!!

    fun cacheName(): String

    fun hazelcastInstance(): HazelcastInstance

    fun getIMap(): IMap<K, E> {
        return hazelcastInstance().getMap(cacheName())
    }

    fun findOne(key: K): Mono<E> {
        logger.debug("[findOne] : $key")
        return Mono.fromCompletionStage { getIMap().getAsync(key!!) }
            .subscribeOn(CacheScheduler.CACHE_SCHEDULER)
    }

    fun findAll(): Flux<E> {
        logger.debug("[findAll]")
        return Mono
            .fromCallable {
                val keys = getIMap().keys
                getIMap().getAll(keys).values
            }
            .flatMapMany {
                if (it.isEmpty()) {
                    Flux.empty()
                } else {
                    Flux.fromIterable(it)
                }
            }
            .subscribeOn(CacheScheduler.CACHE_SCHEDULER)

    }

    fun save(key: K, data: E): Mono<E> {
        logger.debug("[save] : $key")
        return Mono
            .fromCompletionStage {
                getIMap().putAsync(key!!, data!!)
            }
            .thenReturn(data!!)
            .subscribeOn(CacheScheduler.CACHE_SCHEDULER)
    }

}

package com.fistkim.hazelcastsample.repository

import reactor.core.scheduler.Schedulers

/**
 *
 * @author Leo
 */
class CacheScheduler {
    companion object {
        val CACHE_SCHEDULER = Schedulers.newBoundedElastic(100, 100000, "cache-schedulers")
    }
}

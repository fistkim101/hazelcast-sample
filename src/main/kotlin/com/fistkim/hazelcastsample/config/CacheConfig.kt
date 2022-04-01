package com.fistkim.hazelcastsample.config

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 *
 * @author Leo
 */
@Configuration
class CacheConfig {

    @Bean
    fun hazelCastMember() {
        val memberConfig = Config()
        val instance = Hazelcast.newHazelcastInstance(memberConfig)
    }

    @Bean
    fun hazelCastClient() {
        val clientConfig = ClientConfig()
        val hazelCastClient = HazelcastClient.newHazelcastClient(clientConfig)
    }

}

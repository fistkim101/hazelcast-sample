package com.fistkim.hazelcastsample.config

import com.hazelcast.config.*
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.stream.Collectors


@Configuration
@EnableConfigurationProperties(CacheConfig.HazelcastCacheSetting::class)
class CacheConfig(
    val hazelcastCacheSetting: HazelcastCacheSetting
) {

    val logger: Logger = LoggerFactory.getLogger(CacheConfig::class.java)

    @ConstructorBinding // component scan 대상이 된다
    @ConfigurationProperties("cache.hazelcast")
    data class HazelcastCacheSetting(
        val instanceName: String,
        val managementEnabled: Boolean = false,
        val mapSettings: Map<String, HazelcastMapSettings> = emptyMap()
    ) {
        data class HazelcastMapSettings(
            val typeId: Int,
            val backupCount: Int = 0,
            val asyncBackupCount: Int = 0,
            val maxSize: Int = 0,
            val maxSizePolicy: MaxSizePolicy = MaxSizePolicy.PER_NODE,
            val timeToLiveSeconds: Int = 0,
            val className: String,
//            val statisticsEnabled: Boolean = false,
//            val formatObject: Boolean = false,
//            val indexSettings: List<MapIndexSettings> = emptyList()
        ) {
            data class MapIndexSettings(
                val attribute: String,
                val type: IndexType
            )
        }
    }

    @Bean
    fun hazelCastMember(): HazelcastInstance {
        val memberConfiguration = this.hazelCastConfig()
        return Hazelcast.newHazelcastInstance(memberConfiguration)
    }

//    @Bean
//    fun hazelCastClient(): HazelcastInstance {
//        val clientConfig = ClientConfig()
//        return HazelcastClient.newHazelcastClient(clientConfig)
//    }

    private fun hazelCastConfig(): Config {
        val configuration = Config()
            .setInstanceName(hazelcastCacheSetting.instanceName)

        this.applyNetworkSetting(configuration)
        this.applyMapSetting(configuration)
        this.applyManagementSetting(configuration)

        return configuration
    }

    private fun applyNetworkSetting(configuration: Config) {

        val joinConfiguration = configuration.networkConfig.join
        joinConfiguration.multicastConfig.isEnabled = false
        joinConfiguration.tcpIpConfig.isEnabled = true
        joinConfiguration.tcpIpConfig.members = listOf("primary")

    }

    private fun applyMapSetting(configuration: Config) {

        val cacheNames = hazelcastCacheSetting.mapSettings.keys
        if (cacheNames.isEmpty()) {
            return
        }

        val mapConfigurations = cacheNames.stream()
            .map { cacheName -> this.getMapConfiguration(cacheName, cacheNames) }
            .filter { it != null }
            .collect(Collectors.toList())
        if (mapConfigurations.isEmpty()) {
            return
        }

        mapConfigurations
            .forEach {
                logger.info(">>> addMapConfig cache name : ${it!!.name}")
                configuration.addMapConfig(it)
            }
    }

    private fun getMapConfiguration(cacheName: String, cacheNames: Set<String>): MapConfig? {
        val targetKey = cacheNames.firstOrNull { it == cacheName } ?: return null
        val targetMap = hazelcastCacheSetting.mapSettings[targetKey]!!

        val mapConfiguration = MapConfig()
        val evictionConfiguration = EvictionConfig()
            .setMaxSizePolicy(targetMap.maxSizePolicy) // 여러 옵션 가능(https://docs.hazelcast.com/imdg/4.2/data-structures/map#map-eviction)
            .setSize(targetMap.maxSize)
            .setEvictionPolicy(EvictionPolicy.LRU)

        mapConfiguration
            .setName(targetKey)
            .setEvictionConfig(evictionConfiguration)

            // sync backup vs async backup (https://docs.hazelcast.com/hazelcast/5.0/fault-tolerance/backups)
            .setBackupCount(targetMap.backupCount) // Number of synchronous backups.
            .setAsyncBackupCount(targetMap.asyncBackupCount) // Sets the number of asynchronous backups

            .setTimeToLiveSeconds(targetMap.timeToLiveSeconds)

        // external storage 와 연동될때(https://docs.hazelcast.com/hazelcast/5.0/data-structures/working-with-external-data#creating-the-mapstore-implementation)
        //.setMapStoreConfig(MapStoreConfig().setClassName(targetMap.className).setEnabled(true))

        return mapConfiguration
    }

    private fun applyManagementSetting(configuration: Config) {

        if (hazelcastCacheSetting.managementEnabled) {
            val managementConfiguration = configuration.managementCenterConfig
            managementConfiguration.isScriptingEnabled = true
            configuration.managementCenterConfig = managementConfiguration
        }

    }

}

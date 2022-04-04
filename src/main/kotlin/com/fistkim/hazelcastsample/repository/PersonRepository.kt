package com.fistkim.hazelcastsample.repository

import com.fistkim.hazelcastsample.model.Person
import com.hazelcast.core.HazelcastInstance
import org.springframework.stereotype.Component

/**
 *
 * @author Leo
 */
@Component
class PersonRepository(private val hazelCastMember: HazelcastInstance) : CachedRepository<String, Person> {
    override fun cacheName() = "person"

    override fun hazelcastInstance() = hazelCastMember
}

package com.fistkim.hazelcastsample.interfaces

import com.fistkim.hazelcastsample.model.Person
import com.fistkim.hazelcastsample.repository.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class CacheController(private val personRepository: PersonRepository) {

    val logger = LoggerFactory.getLogger(CacheController::class.java)

    @GetMapping("/person/{id}")
    fun findOne(@PathVariable id: String): Mono<Person> {
        return personRepository.findOne(id)
    }

    @GetMapping("/person/all")
    fun getAll(): Flux<Person> {
        return personRepository.findAll()
    }

    @PutMapping("/person/{id}")
    fun save(@PathVariable id: String, @RequestBody person: Person): Mono<Person> {
        logger.info("[save] id: $id || person: $person")
        return personRepository.save(id, person)
    }

}

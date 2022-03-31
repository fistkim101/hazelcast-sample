package com.fistkim.hazelcastsample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HazelcastSampleApplication

fun main(args: Array<String>) {
    runApplication<HazelcastSampleApplication>(*args)
}

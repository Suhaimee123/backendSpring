package com.pimsmart.V1

import com.pimsmart.V1.config.AppConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppConfig::class)

class PimApplication

fun main(args: Array<String>) {
	runApplication<PimApplication>(*args)
}


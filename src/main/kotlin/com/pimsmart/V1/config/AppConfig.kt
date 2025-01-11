package com.pimsmart.V1.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring")
class AppConfig {
    lateinit var folder: FolderConfig
    lateinit var drive: DriveConfig


    class FolderConfig {
        lateinit var id: String
    }

    class DriveConfig {
        lateinit var credentialsPath: String
    }



}

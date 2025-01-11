package com.pimsmart.V1.entities.User

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class RankinActivitykEntities(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val studentId: String,
    val hours: Double? = 0.0,  // Hours for a specific activity or task
    val activityName: String   // Name of the custom activity
)
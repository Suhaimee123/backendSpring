package com.pimsmart.V1.entities.User

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "request")
data class RequestEntities(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,  // Added a comma here

    @Column(name = "student_id")
    val studentId: String? = null,

    @Column(name = "first_name")
    val firstName: String? = null,

    @Column(name = "special_request")
    val specialRequest: String? = null,

    @Column(name = "create_date", updatable = false)
    val createDate: LocalDateTime = LocalDateTime.now() // Set the current time by default

)

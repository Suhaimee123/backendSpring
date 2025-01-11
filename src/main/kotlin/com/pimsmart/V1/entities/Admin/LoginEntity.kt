package com.pimsmart.V1.entities.Admin

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "users")
data class LoginEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "email")
    var email: String = "",

    @Column(name = "password")
    var password: String = "",

    @Column(name = "token")
    var token: String? = null,

    @Column(name = "create_date", updatable = false)
    val createDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "update_date")
    var updateDate: LocalDateTime? = null
) {
    // Default constructor
    constructor() : this(null, "", "", null, LocalDateTime.now(), null)

    @PreUpdate
    fun onUpdate() {
//        updateDate = LocalDateTime.now()
    }
}

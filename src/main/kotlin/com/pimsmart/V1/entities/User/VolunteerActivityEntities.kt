package com.pimsmart.V1.entities.User

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "volunteeractivity")
data class VolunteerActivityEntities(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "student_id")
    val studentId: String? = null,

    @Column(name = "first_name")
    val firstName: String? = null,

    @Column(name = "activity_name")
    val activityName: String? = null,

    @Column(name = "organization_name")
    val organizationName: String? = null,

    @Column(name = "organization_phone")
    val organizationPhone: String? = null,

    @Column(name = "activity_description", columnDefinition = "TEXT")
    val activityDescription: String? = null,

    @Column(name = "activity_date")
    val activityDate: String? = null,

    @Column(name = "hours")
    val hours: Int? = null,

    @Column(name = "create_date", nullable = false)
    val createDate: Timestamp = Timestamp(System.currentTimeMillis()),
    @Column(name = "prefix")
    val prefix: String? = null,

    @Column(name = "nickname")
    val nickname: String? = null,

    @Column(name = "graduate")
    val graduate: String? = null,

    @Column(name = "branch")
    val branch: String? = null,




)

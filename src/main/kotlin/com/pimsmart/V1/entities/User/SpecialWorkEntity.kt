package com.pimsmart.V1.entities.User


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "specialwork")
data class SpecialWorkEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "student_id", length = 255)
    val studentId: String? = null,

    @Column(name = "first_name")
    val firstName: String? = null,

    @Column(name = "work_name", length = 255)
    val workName: String? = null,

    @Column(name = "organization_name", length = 255)
    val organizationName: String? = null,

    @Column(name = "organization_phone")
    val organizationPhone: String? = null,

    @Column(name = "activity_date")
    val activityDate: String? = null,

    @Column(name = "work_type", length = 255)
    val workType: String? = null,

    @Column(name = "work_description")
    val workDescription: String? = null,

    @Column(name = "compensation", precision = 10, scale = 2)
    val compensation: String? = null,

    @Column(name = "work_dates", length = 255)
    val workDates: String? = null,

    @Column(name = "work_time", length = 255)
    val workTime: String? = null,

    @Column(name = "create_date")
    var createDate: LocalDateTime? = null,

    @Column(name = "hour")
    val hours: Int? = null,

    @Column(name = "prefix")
    val prefix: String? = null,

    @Column(name = "nickname")
    val nickname: String? = null,

    @Column(name = "graduate")
    val graduate: String? = null,

    @Column(name = "branch")
    val branch: String? = null,



    )

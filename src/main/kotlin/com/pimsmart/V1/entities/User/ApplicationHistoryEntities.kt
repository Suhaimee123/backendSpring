package com.pimsmart.V1.entities.User

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "application_history")
data class ApplicationHistoryEntities @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val ApplicationId: Int? = null,


    @Column(name = "Register_id")
    var registerId: Long? = null,

    @Column(name = "student_id")
    val studentId: String = "",

    @Column(name = "request_id ")
    val requestId: Int? = null,

    @Column(name = "application_type")
    val applicationType: String = "admin_registration", // กำหนดค่าเริ่มต้น

    @Column(name = "status")
    var status: String? = "pending",

    @Column(name = "submission_date")
    val submissionDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "processed_date")
    var processedDate: LocalDateTime? = null,

    @Column(name = "processed_by")
    var processedBy: String? = null,

    @Column(name = "special_request")
    var specialRequest: String? = null,

    @Column(name = "appointment_date")
    var appointmentDate: String? = null, // New field for the appointment date

    @Column(name = "start_month")
    var startMonth: String? = null, // New field for the start month

    @Column(name = "end_month")
    var endMonth: String? = null // New field for the end month


)


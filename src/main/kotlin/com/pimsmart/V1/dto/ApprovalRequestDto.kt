package com.pimsmart.V1.dto

class ApprovalRequestDto (
    val ApplicationId: Int? = null,
    val studentId: String,
    val approve: String,
    val approvedBy: String,
    val appointmentDate: String? // Nullable since it's optional
    )

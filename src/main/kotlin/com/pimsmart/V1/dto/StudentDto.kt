package com.pimsmart.V1.dto

import java.time.LocalDateTime

data class StudentDto(
    val studentId: String?,
    val firstName: String?
)

data class StudentResponseDto(
    val  id : Int,
    val studentId: String?,
    val firstName: String?,
    val email: String?
)



data class ImageDataResponseDto(
    val name: String, // Added imageName property
    val imageType: String,
    val imageData:String,
    val studentId: String,
    val image: ByteArray

)

data class ImageDataResponseDto1(
    val name: String, // Added imageName property
    val imageType: String,
    val imageData:String,
    val volunteerId: String,
    val image: ByteArray

)

data class ImageDataResponseDto2(
    val name: String, // Added imageName property
    val imageType: String,
    val imageData:String,
    val specialworkId: String,
    val image: ByteArray

)

data class ImageDataResponseDto3(
    val postId: Long?,
    val imageType: String,
    val imageData: String,
    val name: String,
    val createDate: String,
    val content: String,
    val image: ByteArray?
)


data class StudentInfoWithVolunteerHours(
    val id: Int?,
    val studentId: String?,
    val firstName: String?,
    val imageName: String?,
    val imageData: String?,
    val createDate: LocalDateTime?,
    val imageType: String?,
    val totalVolunteerHours: Int
)



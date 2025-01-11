package com.pimsmart.V1.entities.User

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "volunteeractivity_img")
class VolunteerImgEntities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "student_id")
    var studentId: String? = null

    @Column(name = "first_name")
    var firstName: String? = null

    @Column(name = "image_name")
    var imageName: String? = null

    @Column(name = "image_data")
    var imageData: String? = null // Use String for storing base64 or other text representations

    @Column(name = "create_date")
    var createDate: LocalDateTime? = null

    @Column(name = "image_type")
    var imageType: String? = null

    @Column(name = "volunteer_id")
    var volunteerId: String? = null
}
package com.pimsmart.V1.entities.Admin

import com.fasterxml.jackson.annotation.Nulls
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "post_news")
data class PostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "content" )
    var content: String? = null,


    @Column(name = "create_date")
    var createDate: LocalDateTime? = null,

    @Column(name = "image_names")
    var imageNames: String? = null,


    @Column(name = "image_data")
    var imageData: String? = null,

    @Column(name = "image_type")
    var imageType: String? = null
)

package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.PostAdminService
import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.dto.ImageDataResponseDto3
import com.pimsmart.V1.entities.Admin.PostEntity
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/Admin/posts")
class PostNewsController(private val postAdminService: PostAdminService) {

    @GetMapping("/all")
    fun getAllPosts(): ResponseEntity<List<PostEntity>> {
        val posts = postAdminService.getAllPosts()
        return ResponseEntity.ok(posts)
    }

    @GetMapping("/get/{id}")
    fun getPostById(@PathVariable("id") id: Long): ResponseEntity<PostEntity> {
        val post = postAdminService.getPostById(id)
        return if (post != null) {
            ResponseEntity.ok(post)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/downloadAllFiles")
    fun downloadAllFiles(
        @RequestParam("imageType") imageType: String
    ): ResponseEntity<ApiResponse<List<ImageDataResponseDto3>>> {
        return try {
            val response = postAdminService.downloadFilesByImageType(imageType)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = response,
                    message = "Files retrieved successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ApiResponse(
                        success = false,
                        data = null,
                        error = "Error downloading files: ${e.message}"
                    )
                )
        }
    }


    @PutMapping("/update/{id}")
    fun updatePost(
        @PathVariable("id") id: Long,
        @RequestParam("content") content: String,
        @RequestParam("imageFile", required = false) imageFile: MultipartFile?
    ): ResponseEntity<PostEntity> {
        val updatedPost = PostEntity().apply { this.content = content }
        val post = postAdminService.updatePost(id, updatedPost, imageFile)
        return if (post != null) ResponseEntity.ok(post) else ResponseEntity.notFound().build()
    }





    @DeleteMapping("/delete/{id}")
    fun deletePost(@PathVariable("id") id: Long): ResponseEntity<Void> {
        return try {
            postAdminService.deletePost(id)
            ResponseEntity.noContent().build() // ส่งสถานะ 204 (No Content) เมื่อการลบสำเร็จ
        } catch (e: Exception) {
            ResponseEntity.notFound().build() // หากไม่พบโพสต์ที่ต้องการลบ
        }
    }

    @PostMapping("/CreateUpload")
    fun uploadMultipleToGoogleDrive(
        @RequestParam ("files")files: List<MultipartFile>,
        @RequestParam("content") content: String,
        @RequestParam("id") id:String,
        @RequestParam("imageName") imageName: String,
        @RequestParam("imageType") imageType: String
    ): ResponseEntity<List<Res>> {

        val idLong = id.toLongOrNull()
        if (idLong == null) {
            return ResponseEntity.badRequest().body(emptyList())
        }

        val responses = postAdminService.uploadImagesToGoogleDrive(files, content,idLong, imageName, imageType,)
        return ResponseEntity.ok(responses)
    }
}


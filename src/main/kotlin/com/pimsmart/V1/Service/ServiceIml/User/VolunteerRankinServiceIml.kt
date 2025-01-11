package com.pimsmart.V1.service.iml

import com.pimsmart.V1.Service.User.RankinService
import com.pimsmart.V1.entities.User.VolunteerActivityEntities
import com.pimsmart.V1.repository.User.VolunteerActivityRepository
import org.springframework.stereotype.Service

@Service
class VolunteerRankinServiceIml(private val volunteerActivityRepository: VolunteerActivityRepository) : RankinService {

    // Implementing the method as defined in the RankinService interface
    override fun getRankinActivityRanking(studentId: String?): List<Map<String, Any>> {
        // Fetch all volunteer activities from the repository
        val volunteerActivities: List<VolunteerActivityEntities> = volunteerActivityRepository.findAll()

        // Check if there are any activities to rank
        if (volunteerActivities.isEmpty()) {
            println("No volunteer activities found!")
            return emptyList()
        }

        // Create a map to store total hours per student
            val studentHoursMap = mutableMapOf<String, Int>()

        // Calculate total hours per student
        for (activity in volunteerActivities) {
            val sid = activity.studentId ?: continue  // Skip if studentId is null
            val hours = activity.hours ?: 0  // Default to 0 if hours is null
            studentHoursMap[sid] = studentHoursMap.getOrDefault(sid, 0) + hours
        }

        // Sort the students by their total hours in descending order and assign ranks
        val ranking = studentHoursMap.entries
            .sortedByDescending { it.value }
            .mapIndexed { index, entry ->
                mapOf("studentId" to entry.key, "totalHours" to entry.value, "rank" to (index + 1))
            }

        // If a specific studentId is provided, return only that student's rank
        if (!studentId.isNullOrEmpty()) {
            return ranking.filter { it["studentId"] == studentId }
        }

        // Return the full ranking list if no studentId is provided
        return ranking
    }
}

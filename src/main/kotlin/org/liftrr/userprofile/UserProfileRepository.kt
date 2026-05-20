package org.liftrr.userprofile

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
    fun findByUserId(userId: UUID): UserProfile?
}

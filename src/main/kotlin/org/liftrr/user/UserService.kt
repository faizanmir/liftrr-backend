package org.liftrr.user

import org.liftrr.common.UserNotFoundException
import org.liftrr.common.UserNotPersistedException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val userRepository: UserRepository) {

    fun resolveUser(email: String): Pair<User, UUID> {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException(email)
        val id = user.id ?: throw UserNotPersistedException()
        return user to id
    }

    fun updateProfilePictureUrl(email: String, ) {
    }
}
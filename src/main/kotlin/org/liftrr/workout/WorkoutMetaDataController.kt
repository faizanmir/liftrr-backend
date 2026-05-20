package org.liftrr.workout

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class WorkoutController {

    fun createWorkoutSession(
        @AuthenticationPrincipal principal: Principal,

    ) {}
}
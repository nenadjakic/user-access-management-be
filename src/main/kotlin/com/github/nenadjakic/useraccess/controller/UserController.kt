package com.github.nenadjakic.useraccess.controller

import com.github.nenadjakic.useraccess.dto.UserResponse
import com.github.nenadjakic.useraccess.service.UserService
import com.github.nenadjakic.useraccess.util.parseSortOrders
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "User controller", description = "API endpoints for user.")
@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
class UserController(
    private val userService: UserService
) {

    /**
     * Retrieves a paginated list of all users.
     *
     * This endpoint allows an admin to fetch a paginated list of all users.
     * The results can be paginated and sorted based on multiple fields.
     *
     * Only users with the ADMIN role are authorized to access this endpoint.
     *
     * @param page The page number (zero-based) to retrieve. Default is 0.
     * @param size The number of users per page. Default is 10.
     * @param sort A list of sorting parameters in the format "field,direction".
     *             Example: sort=username,asc&sort=email,desc
     * @return ResponseEntity<Page<UserResponse>> representing a paginated list of users.
     */
    @Operation(
        operationId = "getAllUsers",
        summary = "Retrieve paginated list of users",
        description = "Allows an admin to fetch a paginated list of all users. The results can be sorted by multiple fields using the `sort` parameter. Example usage: `sort=username,asc&sort=email,desc`."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved paginated user list."
            ),
            ApiResponse(responseCode = "403", description = "Access denied. Only admins can retrieve users.")
        ]
    )
    @GetMapping
    fun getAllUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id,asc") sort: List<String>
    ): ResponseEntity<Page<UserResponse>> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(parseSortOrders(sort)))
        val users = userService.getAllUsers(pageable).map { user -> UserResponse.from(user) }
        return ResponseEntity.ok(users)
    }

    /**
     * Retrieves user details by ID.
     *
     * This endpoint allows an admin to fetch any user by their unique ID.
     * Regular users can only retrieve their own profile.
     *
     * @param id The unique identifier of the user.
     * @return ResponseEntity<UserResponse> containing the user details.
     */
    @Operation(
        operationId = "getUserById",
        summary = "Retrieve user details by ID",
        description = "Allows an admin to fetch any user's details. Regular users can only access their own profile."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved user details."),
            ApiResponse(responseCode = "403", description = "Access denied. Users can only retrieve their own data."),
            ApiResponse(responseCode = "404", description = "User not found.")
        ]
    )
    @GetMapping("/id")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserResponse> =
        userService.getById(id).let { UserResponse.from(it) }.let { ResponseEntity.ok(it) }

    /**
     * Unlocks a user account.
     *
     * This endpoint allows an admin to unlock a user's account if it was locked due to failed login attempts
     * or other security reasons.
     *
     * @param id The ID of the user to be unlocked.
     * @return ResponseEntity<Void> indicating success.
     */
    @Operation(
        operationId = "unlockUser",
        summary = "Unlock a user account",
        description = "Allows an admin to unlock a user's account if it was locked due to security reasons."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User successfully unlocked."),
            ApiResponse(responseCode = "403", description = "Access denied. Only admins can unlock users."),
            ApiResponse(responseCode = "404", description = "User not found.")
        ]
    )
    @PostMapping("/{id}/unlock")
    fun unlockUser(@PathVariable id: UUID): ResponseEntity<Void> {
        userService.unlockUser(id)
        return ResponseEntity.ok().build()
    }

    /**
     * Disables a user account.
     *
     * This endpoint allows an admin to disable a user's account, preventing them from logging in.
     *
     * @param id The ID of the user to be disabled.
     * @return ResponseEntity<Void> indicating success.
     */
    @Operation(
        operationId = "disableUser",
        summary = "Disable a user account",
        description = "Allows an admin to disable a user's account, preventing them from logging in."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User successfully disabled."),
            ApiResponse(responseCode = "403", description = "Access denied. Only admins can disable users."),
            ApiResponse(responseCode = "404", description = "User not found.")
        ]
    )
    @PostMapping("/{id}/disable")
    fun disableUser(@PathVariable id: UUID): ResponseEntity<Void> {
        userService.disableUser(id)
        return ResponseEntity.ok().build()
    }
}
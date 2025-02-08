package com.github.nenadjakic.useraccess.controller

import com.github.nenadjakic.useraccess.dto.RoleRequest
import com.github.nenadjakic.useraccess.dto.RoleResponse
import com.github.nenadjakic.useraccess.service.RoleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Tag(name = "Role controller", description = "API endpoints for roles.")
@RestController
@RequestMapping("/role")
@PreAuthorize("hasRole('ADMIN')")
@Validated
class RoleController(
    private val roleService: RoleService
) {

    /**
     * Retrieves all roles with pagination.
     *
     * This endpoint returns a paginated list of roles and supports sorting and filtering.
     * Only administrators can access this endpoint.
     *
     * @param page the page number (0-based), default is 0.
     * @param size the number of items per page, default is 10.
     *
     * @return ResponseEntity<Page<RoleResponse>> representing the paginated list of roles.
     */
    @Operation(
        operationId = "getAllRoles",
        summary = "Retrieve all roles with pagination",
        description = "Returns a paginated list of roles."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved roles.")]
    )
    @GetMapping
    fun getAllRoles(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<Page<RoleResponse>> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("name")))
        return ResponseEntity.ok(roleService.find(pageable).map { RoleResponse.from(it) })
    }

    /**
     * Retrieves a specific role by its ID.
     *
     * This endpoint allows an administrator to fetch details of a role
     * using its unique identifier.<
     *
     * @param id the unique identifier of the role.
     * @return @link ResponseEntity containing the role details if found.
     */
    @Operation(
        operationId = "getRoleById",
        summary = "Retrieve a role by ID",
        description = "Fetches a role's details using its unique identifier."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved the role.")]
    )
    @GetMapping("/{id}")
    fun getRoleById(@PathVariable id: UUID): ResponseEntity<RoleResponse> =
        roleService.findById(id)
            .let { ResponseEntity.ofNullable(it?.let { RoleResponse.from(it) }) }

    /**
     * Creates a new role.
     *
     * This endpoint allows administrators to create a new role by providing
     * the necessary details such as role name and permissions.
     * Upon successful creation, the API returns HTTP 201 Created
     * and includes the Location header pointing to the newly created role.
     *
     * @param request The [RoleRequest] containing role details.
     * @return [ResponseEntity] with HTTP 201 Created status and Location header.
     * @throws ResponseStatusException if validation fails or the role already exists.
     */
    @Operation(
        operationId = "createRole",
        summary = "Create a new role",
        description = "Allows administrators to create a new role by providing necessary details " +
                "such as role name and permissions. Returns HTTP 201 Created with Location header."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Role successfully created.",
                headers = [Header(name = "Location", description = "URL of the created role")]),
            ApiResponse(responseCode = "400", description = "Invalid request data or role already exists."),
            ApiResponse(responseCode = "403", description = "Access denied. Only admins can access this.")
        ]
    )
    @PostMapping
    fun createRole(@Valid @RequestBody request: RoleRequest): ResponseEntity<Void> {
        val createdRoleId = roleService.create(request.toRole())

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdRoleId.id)
            .toUri()

        return ResponseEntity.created(location).build()
    }
}
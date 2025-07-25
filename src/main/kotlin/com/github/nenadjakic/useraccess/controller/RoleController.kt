package com.github.nenadjakic.useraccess.controller

import com.github.nenadjakic.useraccess.annotation.CurrentTenantId
import com.github.nenadjakic.useraccess.dto.RoleRequest
import com.github.nenadjakic.useraccess.dto.RoleResponse
import com.github.nenadjakic.useraccess.service.RoleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
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
     * Retrieves all roles for the current tenant with pagination and sorting.
     *
     * This endpoint returns a paginated list of roles belonging to the tenant identified by the JWT 'aud' claim.
     * Supports sorting by role properties. Only administrators can access this endpoint.
     *
     * @param tenantId the UUID of the tenant extracted from the JWT 'aud' claim.
     * @param pageable the pagination and sorting information, defaults to size=25 and sorting by 'name'.
     *
     * @return [ResponseEntity] containing a paginated list of [RoleResponse] objects.
     */
    @Operation(
        operationId = "getAllRoles",
        summary = "Retrieve all roles with pagination",
        description = "Returns a paginated list of roles for the current tenant."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved roles."),
            ApiResponse(responseCode = "401", description = "Unauthorized access."),
            ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions.")
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllRoles(
        @Parameter(
            description = "Tenant ID extracted from JWT 'aud' claim",
            required = true,
            hidden = true
        )
        @CurrentTenantId tenantId: UUID,

        @ParameterObject
        @Parameter(
            description = "Pagination information: page number (0-based)",
            example = "0"
        )
        @PageableDefault(size = 25, sort = ["name"])
        pageable: Pageable,
    ): ResponseEntity<Page<RoleResponse>> =
        ResponseEntity.ok(roleService.find(tenantId, pageable))

    /**
     * Retrieves a specific role by its ID for the current tenant.
     *
     * This endpoint allows an administrator to fetch details of a role
     * identified by its unique ID, but only if the role belongs to the tenant
     * extracted from the JWT 'aud' claim.
     *
     * @param id the unique identifier of the role.
     * @param tenantId the UUID of the tenant extracted from JWT 'aud' claim (injected automatically).
     * @return [ResponseEntity] containing the role details if found and belongs to the tenant,
     *         or 404 Not Found otherwise.
     */
    @Operation(
        operationId = "getRoleById",
        summary = "Retrieve a role by ID",
        description = "Fetches a role's details by its unique identifier, restricted to the current tenant."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved the role."),
            ApiResponse(responseCode = "404", description = "Role not found or does not belong to the tenant."),
            ApiResponse(responseCode = "401", description = "Unauthorized access."),
            ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions.")
        ]
    )
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRoleById(
        @PathVariable
        @Parameter(description = "Unique identifier of the role", required = true)
        id: UUID,

        @CurrentTenantId
        @Parameter(
            description = "Tenant ID extracted from JWT 'aud' claim",
            required = true,
            hidden = true
        )
        tenantId: UUID
    ): ResponseEntity<RoleResponse> =
        ResponseEntity.ofNullable(roleService.findById(tenantId, id))

    /**
     * Creates a new role for the current tenant.
     *
     * This endpoint allows administrators to create a new role by providing
     * the necessary details such as role name and permissions.
     * Upon successful creation, the API returns HTTP 201 Created
     * and includes the Location header pointing to the newly created role resource.
     *
     * @param request the [RoleRequest] containing details of the role to create.
     * @param tenantId the UUID of the tenant extracted from the JWT 'aud' claim (injected automatically).
     *
     * @return [ResponseEntity] with HTTP 201 Created status and Location header
     *         containing the URI of the newly created role.
     *
     * @throws MethodArgumentNotValidException if the [request] data fails validation.
     * @throws RoleAlreadyExistsException if a role with the same name already exists for the tenant.
     */
    @Operation(
        operationId = "createRole",
        summary = "Create a new role",
        description = "Allows administrators to create a new role by providing necessary details " +
                "such as role name and permissions. Returns HTTP 201 Created with Location header."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Role successfully created.",
                headers = [Header(name = "Location", description = "URL of the created role")]
            ),
            ApiResponse(responseCode = "400", description = "Invalid request data or role already exists."),
            ApiResponse(responseCode = "403", description = "Access denied. Only administrators can access this endpoint.")
        ]
    )
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createRole(
        @Valid
        @RequestBody
        @Parameter(description = "Role details to create", required = true)
        request: RoleRequest,

        @CurrentTenantId
        @Parameter(description = "Tenant ID extracted from JWT 'aud' claim", hidden = true)
        tenantId: UUID
    ): ResponseEntity<Void> {
        val createdRoleId = roleService.create(tenantId, request)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdRoleId.id)
            .toUri()

        return ResponseEntity.created(location).build()
    }

}
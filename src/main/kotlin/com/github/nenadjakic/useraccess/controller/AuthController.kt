package com.github.nenadjakic.useraccess.controller

import com.github.nenadjakic.useraccess.dto.*
import com.github.nenadjakic.useraccess.entity.User
import com.github.nenadjakic.useraccess.security.model.LocalUserDetails
import com.github.nenadjakic.useraccess.service.JwtService
import com.github.nenadjakic.useraccess.service.RefreshTokenService
import com.github.nenadjakic.useraccess.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "Authentication controller", description = "API endpoints for user authentication.")
@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val modelMapper: ModelMapper,
    private val userService: UserService,
    private val refreshTokenService: RefreshTokenService,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService
) {

    /**
     * Registers a new user with the provided registration request.
     *
     * This endpoint is used to register a new user based on the provided registration request.
     * The registration request should contain valid user details such as username, email, and password.
     * The request body is validated using the @Valid annotation, ensuring that the provided data meets
     * the required validation rules defined in the RegisterRequest class.
     *
     * @param registerRequest The registration request containing user details.
     * @return ResponseEntity<Void> representing the HTTP response for the registration operation.
     *         Returns ResponseEntity.created() if the registration is successful, otherwise returns an
     *         appropriate error response.
     */
    @Operation(
        operationId = "authRegisterUser",
        summary = "Register a new user.",
        description = "Creates a new user account based on the provided registration request."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User registered successfully.")
        ]
    )
    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<Void> {
        val user = modelMapper.map(registerRequest, User::class.java)
        userService.create(user)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    /**
     * Verify user email based on the provided token.
     *
     * This endpoint is used to verify a user's email based on the token provided as a request parameter.
     * The token is typically sent to the user's email during the registration process for email verification.
     * Once the token is validated, the user's email is verified and appropriate response is returned.
     *
     * @param token The verification token sent to the user's email.
     * @return Returns ResponseEntity.ok() if the email is successfully confirmed,
     *         otherwise returns an appropriate error response.
     */
    @Operation(
        operationId = "authVerifyEmail",
        summary = "Confirm email.",
        description = "Confirms the user's email address based on the provided confirmation token."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Email confirmed successfully")
        ]
    )
    @GetMapping("/verify-email")
    fun verifyEmail(@RequestParam(name = "token") token: String): ResponseEntity<Void> {
        userService.verifyEmail(token)
        return ResponseEntity.ok().build()
    }

    /**
     * Signs in a user based on the provided sign-in request.
     *
     * This endpoint is used to authenticate and sign in a user based on the provided sign-in request.
     * The sign-in request should contain valid credentials such as username/email and password.
     * Upon successful authentication, a JWT token is generated and returned in the response.
     *
     * @param signInRequest The sign-in request containing user credentials.
     * @return ResponseEntity<TokenResponse> representing the HTTP response for sign-in operation.
     *         Returns ResponseEntity.ok() with a TokenResponse if authentication is successful,
     *         otherwise returns an appropriate error response.
     */
    @Operation(
        operationId = "authSignInUser",
        summary = "Sign in user.",
        description = "Signs in a user based on the provided credentials."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User signed in successfully.")
        ]
    )
    @PostMapping("/signin")
    fun signIn(
        @Valid @RequestBody signInRequest: SignInRequest
    ): ResponseEntity<TokenResponse> {
        if (signInRequest.grantType == SignInRequest.GrantType.PASSWORD) {
            val usernamePassword =
                UsernamePasswordAuthenticationToken(signInRequest.username, signInRequest.passwordOrRefreshToken)
            val authUser: Authentication? = authenticationManager.authenticate(usernamePassword)

            return ResponseEntity.ok(createTokenResponse(authUser?.principal as LocalUserDetails))
        } else if (signInRequest.grantType == SignInRequest.GrantType.REFRESH_TOKEN) {
            val refreshTokenEntity =
                refreshTokenService.findByUsernameAndToken(signInRequest.username, signInRequest.passwordOrRefreshToken)
            return ResponseEntity.ok(createTokenResponse(LocalUserDetails(refreshTokenEntity.user)))
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    /**
     * Initiates a password reset request.
     *
     * This endpoint allows a user to request a password reset. If the provided email is associated
     * with an existing account, a password reset link will be sent to that email. The link contains a
     * reset token that must be used to set a new password.
     *
     * For security reasons, this endpoint always returns a success response (200 OK), even if the email
     * is not registered, to prevent enumeration attacks.
     *
     * @param request The password reset request containing the user's email address.
     * @return ResponseEntity<Void> representing the HTTP response for the forgot password operation.
     *         Returns ResponseEntity.ok() to indicate the request was processed.
     */
    @Operation(
        operationId = "authForgotPassword",
        summary = "Request password reset",
        description = "Allows a user to request a password reset. If the provided email is registered, " +
                "a password reset link will be sent to the user's email address. The link contains a " +
                "reset token that must be used to reset the password."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Password reset email sent (even if the email is not registered)."
            ),
            ApiResponse(responseCode = "400", description = "Invalid request format.")
        ]
    )
    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): ResponseEntity<Void> {
        userService.forgotPassword(request)
        return ResponseEntity.ok().build()
    }

    /**
     * Resets the user's password using a provided reset token.
     *
     * This endpoint allows a user to reset their password by providing a valid reset token
     * and a new password. The reset token is generated when the user requests a password reset
     * and is sent to their registered email address.
     *
     * The token must be valid and not expired. If the token is invalid or expired, an error response is returned.
     *
     * @param request The password reset request containing the reset token and the new password.
     * @return ResponseEntity<Void> representing the HTTP response for the reset password operation.
     *         Returns ResponseEntity.ok() if the password reset is successful,
     *         otherwise returns an appropriate error response (e.g., 400 Bad Request if the token is invalid or expired).
     */

    @Operation(
        operationId = "authResetPassword",
        summary = "Reset user password",
        description = "Allows a user to reset their password by providing a valid reset token " +
                "and a new password. The reset token is sent via email when the user requests a password reset. " +
                "If the token is valid, the user's password will be updated."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Password successfully reset.")
        ]
    )
    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): ResponseEntity<Void> {
        userService.resetPassword(request)
        return ResponseEntity.ok().build()
    }


    fun createTokenResponse(user: LocalUserDetails): TokenResponse {
        val accessToken = jwtService.createToken(user)
        val refreshToken = refreshTokenService.create(user.username)!!.token
        return TokenResponse(accessToken, refreshToken)
    }
}

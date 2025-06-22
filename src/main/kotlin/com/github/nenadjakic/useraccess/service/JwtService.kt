package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.config.UserAccessManagementProperties
import com.github.nenadjakic.useraccess.exception.GeneralException
import com.github.nenadjakic.useraccess.security.model.LocalUserDetails
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.OffsetDateTime
import java.util.*
import java.util.stream.Collectors
import javax.crypto.SecretKey

/**
 * Service class for handling JWT tokens.
 */
@Service
open class JwtService(
    private val userAccessManagementProperties: UserAccessManagementProperties
) {
    private val accessTokenValidMinutes = userAccessManagementProperties.jwt.validMinutes

    private fun loadPrivateKey(path: String): PrivateKey {
        val keyBytes = Files.readAllBytes(File(path).toPath())
        val spec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    private fun loadPublicKey(path: String): PublicKey {
        val keyBytes = Files.readAllBytes(File(path).toPath())
        val spec = X509EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    open fun createToken(user: LocalUserDetails, clientId: String): String {
        return createToken(user, clientId, mutableMapOf())
    }

    open fun createToken(user: LocalUserDetails, clientId: String, claims: MutableMap<String, Any>): String {
        val clientConfig = userAccessManagementProperties.clients[clientId]
        if (clientConfig == null) {
            throw GeneralException("Incorrect client id configuration.")
        }

        val now = OffsetDateTime.now()
        val created = Date(now.toEpochSecond() * 1000)
        val expireAt = Date((now.toEpochSecond() + (accessTokenValidMinutes * 60)) * 1000)

        val roles = user.authorities.stream().map { it.authority } .collect(Collectors.toList())
        claims["roles"] = roles
        claims["aud"] = clientId

        return Jwts
            .builder()
            .claims(claims)
            .subject(user.username)
            .issuedAt(created)
            .expiration(expireAt)
            .signWith(loadPrivateKey(clientConfig.privateKeyPath))
            .compact()
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token string.
     * @return An instance of Claims containing all extracted claims from the token.
     * @throws io.jsonwebtoken.JwtException if there is an error while extracting claims from the token.
     */
    open fun extractAllClaims(token: String): Claims {
        val clientIds = extractAud(token)
        if (clientIds.isNullOrEmpty()) {
            throw IllegalArgumentException()
        }


        val publicKey = loadPublicKey(clientIds.stream().findFirst().get())

        return Jwts
            .parser()
            .verifyWith(publicKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * Extracts a specific claim from the JWT token using the provided claims resolver function.
     *
     * @param token The JWT token string.
     * @param claimsResolver The claims resolver function to extract the desired claim.
     * @return The extracted claim value of type T, or null if the claim is not found or extraction fails.
     * @throws io.jsonwebtoken.JwtException if there is an error while extracting the claim.
     * @param T The type of the claim value to extract.
     */
    private fun <T> extractClaim(token: String, claimsResolver: Function1<Claims?, T?>): T? {
        val claims = extractAllClaims(token)
        return claimsResolver.invoke(claims)
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token The JWT token string.
     * @return The extracted username as a String, or null if the username is not found.
     * @throws io.jsonwebtoken.JwtException if there is an error while extracting the username.
     */
    private fun extractUserName(token: String): String? {
        return extractClaim(token) { x -> x?.subject }
    }

    private fun extractAud(token: String): Set<String>? {
        return extractClaim(token) { x -> x?.audience }
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token The JWT token string.
     * @return true if the token is expired, false otherwise.
     * @throws io.jsonwebtoken.JwtException if there is an error while checking the token expiration.
     */
    private fun isTokenExpired(token: String): Boolean {
        val expireAt = extractClaim(token) { x -> x?.expiration}
        return expireAt?.before(Date()) ?: false
    }

    /**
     * Checks if the JWT token is valid.
     *
     * @param token The JWT token string.
     * @return true if the token is valid, false otherwise.
     * @throws io.jsonwebtoken.JwtException if there is an error while checking the token validity.
     */
    open fun isValid(token: String, expectedClientId: String): Boolean {
        try {
            val claims = extractAllClaims(token)

            val expireAt = claims.expiration
            if (expireAt.before(Date())) {
                return false
            }

            return expectedClientId == claims["aud"] as? String
        } catch (ex: Exception) {
            return false
        }
    }
}
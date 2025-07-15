package com.github.nenadjakic.useraccess.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.nenadjakic.useraccess.config.UserAccessManagementProperties
import com.github.nenadjakic.useraccess.entity.Tenant
import com.github.nenadjakic.useraccess.security.model.LocalUserDetails
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.OffsetDateTime
import java.util.*
import java.util.stream.Collectors

/**
 * Service class for handling JWT tokens.
 */
@Service
class JwtService(
    private val tenantService: TenantService,
    private val userAccessManagementProperties: UserAccessManagementProperties,
    private val objectMapper: ObjectMapper
) {
    private val accessTokenValidMinutes = userAccessManagementProperties.jwt.validMinutes

    @Volatile
    private var clients: Map<UUID, Tenant> = emptyMap()

    @PostConstruct
    fun init() {
        refreshClients()
    }

    @Scheduled(cron = "0 0 0 * * ?")
    fun refreshClients() {
        clients = tenantService.findAll().associateBy { it.id!! }
    }

    private fun loadPrivateKey(path: String): PrivateKey {
        val pem = File(path).readText()
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val keyBytes = Base64.getDecoder().decode(pem)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    private fun loadPublicKey(path: String): PublicKey {
        val pem = File(path).readText()
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val keyBytes = Base64.getDecoder().decode(pem)
        val spec = X509EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    fun createToken(user: LocalUserDetails, clientId: String): String {
        return createToken(user, clientId, mutableMapOf())
    }

    fun createToken(user: LocalUserDetails, clientId: String, claims: MutableMap<String, Any>): String {
        val client = clients[UUID.fromString(clientId)] ?: throw IllegalArgumentException("Client not found")

        val privateKey = loadPrivateKey(client.privateKeyPath)

        val now = OffsetDateTime.now()
        val created = Date(now.toEpochSecond() * 1000)
        val expireAt = Date((now.toEpochSecond() + (accessTokenValidMinutes * 60)) * 1000)

        val roles = user.authorities.stream().map { it.authority } .collect(Collectors.toList())
        claims["roles"] = roles
        claims["aud"] = clientId
        claims["iss"] = userAccessManagementProperties.jwt.issuer

        return Jwts
            .builder()
            .claims(claims)
            .subject(user.username)
            .issuedAt(created)
            .expiration(expireAt)
            .signWith(privateKey)
            .compact()
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token string.
     * @return An instance of Claims containing all extracted claims from the token.
     * @throws io.jsonwebtoken.JwtException if there is an error while extracting claims from the token.
     */
    fun extractAllClaims(token: String): Claims {
        val clientIds = extractAudWithoutSignature(token)
        if (clientIds.isNullOrEmpty()) {
            throw IllegalArgumentException()
        }

        val client = clients[UUID.fromString(clientIds)] ?: throw IllegalArgumentException("Client not found")
        val publicKey = loadPublicKey(client.publicKeyPath)

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

    private fun extractAudWithoutSignature(token: String): String? {
        val parts = token.split('.')
        if (parts.size < 2) throw IllegalArgumentException("Invalid JWT token format")

        val payloadB64 = parts[1]
        val decodedBytes = Base64.getUrlDecoder().decode(payloadB64)
        val payloadJson = String(decodedBytes)

        val claimsMap: Map<String, Any> = objectMapper.readValue(payloadJson, Map::class.java) as Map<String, Any>

        val audClaim = claimsMap["aud"] ?: return null

        return when (audClaim) {
            is String -> audClaim
            is List<*> -> audClaim.firstOrNull() as? String
            else -> null
        }
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
    fun isValid(token: String): Boolean {
        try {
            val claims = extractAllClaims(token)

            val expireAt = claims.expiration
            return !expireAt.before(Date())
        } catch (ex: Exception) {
            return false
        }
    }
}
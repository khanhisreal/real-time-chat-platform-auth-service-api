package lucas.personal.realtime_chat.auth_service_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    private final String testSecret =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    private final Long testExpirationMs = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", testExpirationMs);
    }

    @Test
    @DisplayName("generateToken() - Should build valid JWT with subject, userId claim, and correct dates")
    void generateToken_ShouldReturnValidJwtToken() {
        Long userId = 100L;
        String username = "lucas_dev";
        String token = jwtService.generateToken(userId, username);

        assertThat(token).isNotBlank();

        byte[] keyBytes = Decoders.BASE64.decode(testSecret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("lucas_dev");
        assertThat(claims.get("userId", Long.class)).isEqualTo(100L);
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    @DisplayName("extractUsername() - Should successfully extract subject from token")
    void extractUsername_ShouldReturnUsername() {
        String token = jwtService.generateToken(100L, "lucas_dev");

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("lucas_dev");
    }

    @Test
    @DisplayName("isTokenValid() - Should return true when username matches and token is unexpired")
    void isTokenValid_ValidTokenAndUsername_ReturnsTrue() {
        String token = jwtService.generateToken(100L, "lucas_dev");

        boolean isValid = jwtService.isTokenValid(token, "lucas_dev");

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid() - Should return false when username does not match token subject")
    void isTokenValid_WrongUsername_ReturnsFalse() {
        String token = jwtService.generateToken(100L, "lucas_dev");

        boolean isValid = jwtService.isTokenValid(token, "other_user");

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("extractClaim() - Should extract custom claim using claim resolver")
    void extractClaim_ShouldReturnClaimValue() {
        String token = jwtService.generateToken(100L, "lucas_dev");

        Long userId = jwtService.extractClaim(
                token,
                claims -> claims.get("userId", Long.class)
        );

        assertThat(userId).isEqualTo(100L);
    }

    @Test
    @DisplayName("isTokenValid() - Should throw ExpiredJwtException when parsing expired token")
    void isTokenValid_ExpiredToken_ThrowsExpiredJwtException() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -10000L);
        String expiredToken = jwtService.generateToken(100L, "lucas_dev");

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, "lucas_dev"))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
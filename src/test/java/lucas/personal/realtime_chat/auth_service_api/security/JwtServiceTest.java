package lucas.personal.realtime_chat.auth_service_api.security;

import io.jsonwebtoken.Claims;
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

class JwtServiceTest {

    private JwtService jwtService;

    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

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

}
package lucas.personal.realtime_chat.auth_service_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("doFilterInternal() - Should skip authentication when Authorization header is missing")
    void doFilterInternal_MissingHeader_SkipsAuth() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("doFilterInternal() - Should skip authentication when header does not start with 'Bearer '")
    void doFilterInternal_InvalidHeaderPrefix_SkipsAuth() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("doFilterInternal() - Should authenticate user when token is valid")
    void doFilterInternal_ValidToken_AuthenticatesUser() throws ServletException, IOException {
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("testuser");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal() - Should not authenticate user when token is invalid or expired")
    void doFilterInternal_InvalidToken_DoesNotAuthenticate() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal() - Should handle exception gracefully during token parsing")
    void doFilterInternal_TokenParseException_ProceedsWithoutAuth() throws ServletException, IOException {
        String token = "malformed.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Signature verification failed"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("doFilterInternal() - Should skip auth when extracted username is null")
    void doFilterInternal_NullUsername_SkipsAuth() throws ServletException, IOException {
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("doFilterInternal() - Should skip re-authentication when user is already in SecurityContext")
    void doFilterInternal_AlreadyAuthenticated_SkipsAuth() throws ServletException, IOException {
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("testuser");

        org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("doFilterInternal() - Should throw NullPointerException when request is null")
    void doFilterInternal_NullRequest_ThrowsNpe() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                jwtAuthenticationFilter.doFilterInternal(null, response, filterChain)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("doFilterInternal() - Should throw NullPointerException when response is null")
    void doFilterInternal_NullResponse_ThrowsNpe() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                jwtAuthenticationFilter.doFilterInternal(request, null, filterChain)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("doFilterInternal() - Should throw NullPointerException when filterChain is null")
    void doFilterInternal_NullFilterChain_ThrowsNpe() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                jwtAuthenticationFilter.doFilterInternal(request, response, null)
        ).isInstanceOf(NullPointerException.class);
    }
}
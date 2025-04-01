package com.tsore.user.controller;

import com.tsore.user.dto.FirebaseAuthRequest;
import com.tsore.user.dto.LoginRequest;
import com.tsore.user.dto.LoginResponse;
import com.tsore.user.service.AuthService;
import com.tsore.user.service.FirebaseAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Operation(summary = "Test endpoint", description = "Simple hello endpoint for testing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/hello")
    public String hello (){
        return "hello";
    }

    @Operation(summary = "User login", description = "Authenticate a user with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful authentication", 
                     content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "User registration", description = "Register a new user with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully registered", 
                     content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or username already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse>  signup(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @Operation(summary = "Firebase authentication", description = "Authenticate a user with Firebase credentials")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful Firebase authentication", 
                     content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid Firebase credentials"),
        @ApiResponse(responseCode = "500", description = "Firebase authentication error")
    })
    @PostMapping("/firebase")
    public ResponseEntity<LoginResponse>   firebaseLogin(@RequestBody FirebaseAuthRequest request) throws Exception {
        return ResponseEntity.ok(firebaseAuthService.authenticateWithFirebase(request));
    }

    @Operation(summary = "Get user ID from token", description = "Extract the user ID from a valid JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User ID successfully extracted", 
                     content = @Content(schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    @GetMapping("/get_user_id")
    public ResponseEntity<Long> getUserIdFromToken(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");

        Long userId = authService.getUserIdFromToken(token);

        return ResponseEntity.ok(userId);
    }
}
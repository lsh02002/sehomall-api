package com.example.sehomallapi.web.controller.users;

import com.example.sehomallapi.config.security.JwtTokenProvider;
import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.exceptions.AccessDeniedException;
import com.example.sehomallapi.service.exceptions.NotAcceptableException;
import com.example.sehomallapi.service.users.UserService;
import com.example.sehomallapi.web.dto.users.LoginRequest;
import com.example.sehomallapi.web.dto.users.SignupRequest;
import com.example.sehomallapi.web.dto.users.UserInfoResponse;
import com.example.sehomallapi.web.dto.users.UserResponse;
import com.example.sehomallapi.web.dto.users.userLoginHist.UserLoginHistResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signUp(@RequestBody SignupRequest signupRequest){
        return ResponseEntity.ok(userService.signUp(signupRequest));
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse){
        List<Object> accessTokenAndRefreshTokenAndResponse = userService.login(loginRequest);
        jwtTokenProvider.setAccessTokenCookies(httpServletResponse, (String) accessTokenAndRefreshTokenAndResponse.get(0));
        jwtTokenProvider.setRefreshTokenCookies(httpServletResponse, (String) accessTokenAndRefreshTokenAndResponse.get(1));

        return (UserResponse) accessTokenAndRefreshTokenAndResponse.get(2);
    }

    @DeleteMapping("/logout")
    public UserResponse logout(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletRequest request, HttpServletResponse response){
        return userService.logout(customUserDetails.getEmail(), request, response);
    }

    @DeleteMapping("/withdrawal")
    public UserResponse withdrawal(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return userService.withdrawal(customUserDetails.getEmail());
    }

    @GetMapping("/info")
    public UserInfoResponse getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return userService.getUserInfo(customUserDetails);
    }

    @GetMapping("/is-nickname-existed/{nickname}")
    public boolean isNicknameExisted(@PathVariable(name = "nickname") String nickname){
        return userService.isNicknameExisted(nickname);
    }

    @GetMapping("/is-email-existed/{email}")
    public boolean isEmailExisted(@PathVariable(name = "email") String email){
        return userService.isEmailExisted(email);
    }

    @GetMapping("/hist")
    public Page<UserLoginHistResponse> getUserLoginHist(@AuthenticationPrincipal CustomUserDetails customUserDetails, Pageable pageable) {
        return userService.getUserLoginHist(customUserDetails.getId(), pageable);
    }

    @GetMapping(value = "/entrypoint")
    public void entrypointException(@RequestParam(name = "token", required = false) String token) {
        if (token==null) throw new NotAcceptableException("로그인(Jwt 토큰)이 필요합니다.", null);
        else throw new NotAcceptableException("로그인(Jwt 토큰)이 만료 되었습니다. 다시 로그인 하세요", null);
    }

    @GetMapping(value = "/access-denied")
    public void accessDeniedException(@RequestParam(name = "roles", required = false) String roles) {
        if(roles==null) throw new AccessDeniedException("권한이 설정되지 않았습니다.",null);
        else throw new AccessDeniedException("권한이 없습니다.", "시도한 유저의 권한 : "+roles);
    }

    @GetMapping("/test1")
    public Object test1(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return customUserDetails.toString();
    }

    @GetMapping("/test2")
    public String test2(){
        return "Jwt 토큰이 상관없는 EntryPoint 테스트입니다.";
    }

    //관리자 모듈

    @PostMapping("/admin-login")
    public UserResponse adminLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse){
        List<Object> accessTokenAndRefreshTokenAndResponse = userService.adminLogin(loginRequest);
        jwtTokenProvider.setAccessTokenCookies(httpServletResponse, (String) accessTokenAndRefreshTokenAndResponse.get(0));
        jwtTokenProvider.setRefreshTokenCookies(httpServletResponse, (String) accessTokenAndRefreshTokenAndResponse.get(1));

        return (UserResponse) accessTokenAndRefreshTokenAndResponse.get(2);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/all-users-info")
    public Page<UserInfoResponse> getAllUsersInfo(Pageable pageable) {
        return userService.getAllUsersInfo(pageable);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/hist/{userId}")
    public Page<UserLoginHistResponse> getUserLoginHistByAdmin(@PathVariable Long userId, Pageable pageable) {
        return userService.getUserLoginHist(userId, pageable);
    }
}

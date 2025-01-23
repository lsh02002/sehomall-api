package com.example.sehomallapi.service.users;

import com.example.sehomallapi.config.security.JwtTokenProvider;
import com.example.sehomallapi.repository.cart.Cart;
import com.example.sehomallapi.repository.cart.CartRepository;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.repository.users.userRoles.Roles;
import com.example.sehomallapi.repository.users.userRoles.RolesRepository;
import com.example.sehomallapi.repository.users.userRoles.UserRoles;
import com.example.sehomallapi.repository.users.userRoles.UserRolesRepository;
import com.example.sehomallapi.service.exceptions.BadRequestException;
import com.example.sehomallapi.service.exceptions.ConflictException;
import com.example.sehomallapi.service.exceptions.CustomBadCredentialsException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.web.dto.users.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final UserRolesRepository userRolesRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CartRepository cartRepository;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void insertRoleUserAndRoleAdminToNewDb(){
        //db를 새로 생성할 때 roles(ROLE_USER)초기값 생성
        Roles roleUser = rolesRepository.findByName("ROLE_USER");

        if(roleUser == null){
            rolesRepository.save(Roles.builder()
                    .name("ROLE_USER")
                    .build());
        }

        //db를 새로 생성할 때 roles(ROLE_ADMIN)초기값 생성
        Roles roleAdmin = rolesRepository.findByName("ROLE_ADMIN");

        if(roleAdmin == null){
            rolesRepository.save(Roles.builder()
                    .name("ROLE_ADMIN")
                    .build());
        }
    }

    @Transactional
    public UserResponse signUp(SignupRequest signupRequest){
        String email = signupRequest.getEmail();
        String password = signupRequest.getPassword();

        if(!email.matches(".+@.+\\..+")){
            throw new BadRequestException("이메일을 정확히 입력해주세요.", email);
        } else if (signupRequest.getNickname().matches("01\\d{9}")){
            throw new BadRequestException("전화번호를 이름으로 사용할수 없습니다.",signupRequest.getNickname());
        }

        if(userRepository.existsByEmail(email)){
            throw new ConflictException("이미 입력하신 " + email + " 이메일로 가입된 계정이 있습니다.", email);
        } else if(signupRequest.getNickname().trim().isEmpty() || signupRequest.getNickname().length()>30){
            throw new BadRequestException("닉네임은 비어있지 않고 30자리 이하여야 합니다.", signupRequest.getNickname());
        } else if(signupRequest.getName().trim().isEmpty() || signupRequest.getName().length()>30){
            throw new BadRequestException("이름은 비어있지 않고 30자리 이하여야 합니다.", signupRequest.getName());
        } else if(!signupRequest.getPhoneNumber().matches("01\\d{9}")){
            throw new BadRequestException("전화번호 형식이 올바르지 않습니다.", signupRequest.getPhoneNumber());
        } else if(userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())){
            throw new ConflictException("이미 입력하신 "+signupRequest.getPhoneNumber()+" 전화번호로 가입된 계정이 있습니다.",signupRequest.getPhoneNumber());
        }else if(!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$")
                ||!(password.length()>=8&&password.length()<=20)){
            throw new BadRequestException("비밀번호는 8자 이상 20자 이하 숫자와 영문소문자 조합 이어야 합니다.",password);
        } else if(!signupRequest.getPasswordConfirm().equals(password)) {
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.","password : "+password+", password_confirm : "+signupRequest.getPasswordConfirm());
        } else if(signupRequest.getGender() == null || !(signupRequest.getGender().equals("남성") || signupRequest.getGender().equals("여성"))){
            throw new BadRequestException("성별 형식이 올바르지 않습니다.", signupRequest.getGender());
        }

        signupRequest.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        Roles roles = rolesRepository.findByName("ROLE_USER");

        LocalDateTime birthDate = null;

        if(signupRequest.getBirthDate() != null) {
            LocalDate date = LocalDate.parse(signupRequest.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            birthDate = date.atStartOfDay();
        }

        User user = User.builder()
                .email(signupRequest.getEmail())
                .name(signupRequest.getName())
                .password(signupRequest.getPassword())
                .nickname(signupRequest.getNickname())
                .phoneNumber(signupRequest.getPhoneNumber())
                .address(signupRequest.getAddress())
                .gender(signupRequest.getGender())
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);

        userRolesRepository.save(UserRoles.builder()
                .user(user)
                .roles(roles)
                .build());

        Cart cart = Cart.builder().user(savedUser).build();
        cartRepository.save(cart);

        SignupResponse signupResponse = SignupResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();

        return new UserResponse(HttpStatus.OK.value(), user.getNickname() + "님 회원 가입 완료 되었습니다.", signupResponse);
    }

    public List<Object> login(LoginRequest request) {
        if(request.getEmail()==null||request.getPassword()==null){
            throw new BadRequestException("이메일이나 비밀번호 값이 비어있습니다.","email : "+request.getEmail()+", password : "+request.getPassword());
        }
        User user;

        if(request.getEmail().matches(".+@.+\\..+")) {
            user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("입력하신 이메일의 계정을 찾을 수 없습니다.", request.getEmail()));
        } else {
            throw new BadRequestException("이메일이 잘못 입력되었습니다.", request.getEmail());
        }
        String p1 = user.getPassword();

        if(!passwordEncoder.matches(request.getPassword(), p1)){
            throw new CustomBadCredentialsException("비밀번호가 일치하지 않습니다.", request.getPassword());
        }

        List<String> roles = user.getUserRoles().stream()
                .map(UserRoles::getRoles).map(Roles::getName).toList();

        SignupResponse signupResponse = SignupResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();

        UserResponse authResponse = new UserResponse(HttpStatus.OK.value(), "로그인에 성공 하였습니다.", signupResponse);

        return Arrays.asList(jwtTokenProvider.createToken(user.getEmail()), authResponse);
    }

    public UserInfoResponse getUserInfo(CustomUserDetails customUserDetails) {
        String birthDate = customUserDetails.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        String createAt = customUserDetails.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        return UserInfoResponse.builder()
                .nickname(customUserDetails.getNickname())
                .name(customUserDetails.getName())
                .email(customUserDetails.getEmail())
                .address(customUserDetails.getAddress())
                .phoneNumber(customUserDetails.getPhoneNumber())
                .gender(customUserDetails.getGender())
                .birthDate(birthDate)
                .createAt(createAt)
                .build();
    }

    public boolean isNicknameExisted(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    public boolean isEmailExisted(String email){
        return userRepository.existsByEmail(email);
    }
}

package com.example.sehomallapi.repository.users.userDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@ToString
public class CustomUserDetails implements UserDetails {
    @Getter
    private Long id;
    @Getter
    private String phoneNumber;
    @Getter
    private String nickname;

    @Getter
    private String email;
    private String password;

    @Getter
    private String address;
    @Getter
    private String gender;

    @Getter
    @JsonProperty("birthDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화 시 필요
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화 시 필요
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime birthDate;

    @Getter
    @JsonProperty("createAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화 시 필요
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화 시 필요
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createAt;

    private List<String> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

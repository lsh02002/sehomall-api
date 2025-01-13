package com.example.sehomallapi.repository.users;

import com.example.sehomallapi.repository.cart.Cart;
import com.example.sehomallapi.repository.payment.Payment;
import com.example.sehomallapi.repository.users.userRoles.UserRoles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(of = "userId")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column
    private String address;

    @Column
    private String gender;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "user")
    private Collection<UserRoles> userRoles;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Cart cart;
}

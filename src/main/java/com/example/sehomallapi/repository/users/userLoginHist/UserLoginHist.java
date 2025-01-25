package com.example.sehomallapi.repository.users.userLoginHist;

import com.example.sehomallapi.repository.payment.PaymentItem;
import com.example.sehomallapi.repository.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_login_hists")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserLoginHist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_at")
    private LocalDateTime loginAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

package com.example.sehomallapi.repository.payment;

import com.example.sehomallapi.repository.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_sum", nullable = false)
    private int productSum;

    @Column(nullable = false)
    private String email;

    @Column(name = "delivery_name", nullable = false)
    private String deliveryName;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_phone", nullable = false)
    private String deliveryPhone;

    @Column(name = "delivery_message")
    private String deliveryMessage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<PaymentItem> paymentItems = new ArrayList<>();
}

//package com.example.sehomallapi.repository.review;
//
//import com.example.sehomallapi.repository.item.Item;
//import com.example.sehomallapi.repository.users.User;
//import jakarta.persistence.*;
//import lombok.*;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "reviews")
//@Getter
//@Setter
//@Builder
//@EqualsAndHashCode(of = "id")
//@AllArgsConstructor
//@NoArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
//public class Review {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(columnDefinition = "TEXT", nullable = false)
//    private String content;
//
//    @Column(nullable = false)
//    private Integer rating;
//
//    @CreatedDate
//    @Column(name = "create_at")
//    private LocalDateTime createdAt;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "item_id")
//    private Item item;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//}

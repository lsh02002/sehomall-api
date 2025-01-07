package com.example.sehomallapi.repository.heart;

import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.users.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "heart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Heart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
}

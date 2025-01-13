package com.example.sehomallapi.repository.item;

import com.example.sehomallapi.repository.review.Review;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "files")
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "file_extension", nullable = false)
    private String fileExtension;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "item_key")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "review_key")
    private Review review;
}

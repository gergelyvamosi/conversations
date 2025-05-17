package org.nextweb.converstions.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdTimestamp;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "user_a_id", nullable = false)
    private User userA;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "user_b_id", nullable = false)
    private User userB;

    @Column(nullable = false)
    private String text;

    @Builder.Default
    @Column(nullable = false, length = 1)
    private String archived = "N";

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

     public void archive() {
        this.archived = "Y";
    }

    public void unarchive() {
        this.archived = "N";
    }
}

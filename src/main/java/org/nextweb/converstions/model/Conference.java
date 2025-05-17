package org.nextweb.converstions.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;

@Entity
@Table(name = "conferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Builder.Default
    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "conference_users",
            joinColumns = @JoinColumn(name = "conference_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {
                @UniqueConstraint(columnNames = {"conference_id", "user_id"})
            }
    )
    private List<User> users = new ArrayList<>();

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Conversation> conversations = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate plannedDate;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

     public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }
}

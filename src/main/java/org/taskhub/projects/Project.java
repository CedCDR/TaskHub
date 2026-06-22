package org.taskhub.projects;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.taskhub.entities.BaseEntity;
import org.taskhub.tasks.Task;
import org.taskhub.users.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Nonnull
    private String name;

    @Nonnull
    private long  projectNumber;

    @ManyToOne
    @JoinColumn(name = "project_lead_id")
    private User projectLead;

    @ManyToMany
    @JoinTable(
            name = "project_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers = new HashSet<>();

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private ProjectProgress progress = ProjectProgress.ACTIVE;

}

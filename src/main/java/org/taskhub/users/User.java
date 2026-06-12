package org.taskhub.users;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.taskhub.roles.Role;
import org.taskhub.tasks.Task;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Nonnull
    private String firstName;

    @Nonnull
    private String secondName;

    @Nonnull
    private String email;

    private String phoneNumber;

    private String profilePictureURL;

    private Set<Role> roles = new HashSet<Role>();

    private Set<Task> assignedTasks = new HashSet<Task>();

    //Hilfsmethoden
    public void addRole(Role role)
    {
        this.roles.add(role);
    }
}

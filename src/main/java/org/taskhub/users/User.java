package org.taskhub.users;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.taskhub.roles.Role;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor

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

    private Set<Role> roles;

    //private Set<Task> assignedTasks;

}

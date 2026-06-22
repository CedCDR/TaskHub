package org.taskhub.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.taskhub.entities.BaseEntity;
import org.taskhub.roles.Role;
import org.taskhub.tasks.Task;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "users")
public class User extends BaseEntity {

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

    @ManyToMany
    private Set<Role> roles = new HashSet<Role>();

    //Hilfsmethoden
    public void addRole(Role role)
    {
        this.roles.add(role);
    }

    public void removeRole(Role role)
    {
        this.roles.remove(role);
    }
}

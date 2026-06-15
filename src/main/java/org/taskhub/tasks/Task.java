package org.taskhub.tasks;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.taskhub.users.User;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Nonnull
    private String name;

    private String description;

    private Date dueDate;

    //private Enum progress

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User responsibleUser;

    @Nonnull
    public Date createdAt;

    @Nonnull
    public Date updatedAt;
}

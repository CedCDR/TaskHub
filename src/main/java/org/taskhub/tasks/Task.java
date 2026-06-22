package org.taskhub.tasks;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.taskhub.entities.BaseEntity;
import org.taskhub.projects.Project;
import org.taskhub.users.User;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Nonnull
    private String name;

    private String description;

    private Date dueDate;

    //Durch @Enumerated(EnumType.STRING) wird das Enum als String in der Datenbank gespeichert, nicht als Int
    @Enumerated(EnumType.STRING)
    private TaskProgress progress = TaskProgress.UNASSIGNED;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User responsibleUser;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}

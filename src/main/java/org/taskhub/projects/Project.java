package org.taskhub.projects;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.taskhub.entities.BaseEntity;
import org.taskhub.tasks.Task;
import org.taskhub.users.User;

import java.util.ArrayList;
import java.util.Date;

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

    @Nonnull
    private String projectLead;

    private ArrayList<User> assignedUsers;

    private ArrayList<Task> tasks;

    private Boolean isActive;

    //public Enum progress;

}

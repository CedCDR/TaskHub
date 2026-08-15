package org.taskhub.tasks.dto;

import org.taskhub.tasks.Task;
import org.taskhub.tasks.TaskProgress;
import org.taskhub.users.User;
import org.taskhub.users.dto.UserSummaryDto;

import java.util.Date;

public record TaskDetailDto(
        Long id,

        String name,

        String description,

        Date dueDate,

        TaskProgress progress,

        UserSummaryDto responsibleUser,

        Long projectId
)
{
    public static TaskDetailDto fromEntity(Task task)
    {
        //Erlaubt das einfache Konvertieren von Task Objekt zur DTO
        //Enthält nur UserSummaryDto und projectId um Overfetching zu verhindern
        UserSummaryDto userDto = null;
        if (task.getResponsibleUser() != null)
        {
            User user = task.getResponsibleUser();
            userDto = new UserSummaryDto(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getProfilePictureURL()
            );
        }

        Long projectId = null;
        if (task.getProject() != null)
        {
            projectId = task.getProject().getId();
        }

        Date dueDateStr = null;
        if (task.getDueDate() != null)
        {
            dueDateStr = task.getDueDate();
        }

        return new TaskDetailDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                dueDateStr,
                task.getProgress(),
                userDto,
                projectId
        );
    }
}

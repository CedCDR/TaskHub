package org.taskhub.tasks.dto;

import org.taskhub.tasks.Task;
import org.taskhub.tasks.TaskProgress;
import org.taskhub.users.User;
import org.taskhub.users.dto.UserSummaryDto;

import java.util.Date;

public record TaskSummaryDto(
        String name,

        TaskProgress progress,

        Date dueDate,

        UserSummaryDto responsibleUser,

        Long projectId
) {
    public static TaskSummaryDto fromEntity(Task task)
    {
        UserSummaryDto userDto = null;
        if (task.getResponsibleUser() != null)
        {
            User user = task.getResponsibleUser();
            userDto = UserSummaryDto.fromEntity(user);
        }

        Long projectId = null;
        if (task.getProject() != null)
        {
            projectId = task.getProject().getId();
        }

        return new TaskSummaryDto(
                task.getName(),
                task.getProgress(),
                task.getDueDate(),
                userDto,
                projectId
        );
    }
}

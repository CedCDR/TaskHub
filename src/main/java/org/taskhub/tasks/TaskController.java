package org.taskhub.tasks;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.taskhub.tasks.dto.TaskDetailDto;
import org.taskhub.tasks.dto.TaskSummaryDto;
import org.taskhub.users.User;
import org.taskhub.users.dto.UserSummaryDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDetailDto> getTaskById (@PathVariable Long id)
    {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(TaskDetailDto.fromEntity(task));
    }

    @GetMapping()
    public ResponseEntity<List<TaskSummaryDto>> getAllTasks()
    {
        List<Task> tasks = taskService.getAllTasks();

        List<TaskSummaryDto> response = tasks.stream()
                .map(TaskSummaryDto::fromEntity).toList();

        return ResponseEntity.ok(response);
    }
}

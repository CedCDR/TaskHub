package org.taskhub.tasks;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.taskhub.tasks.dto.TaskDetailDto;
import org.taskhub.users.User;
import org.taskhub.users.dto.UserSummaryDto;

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
}

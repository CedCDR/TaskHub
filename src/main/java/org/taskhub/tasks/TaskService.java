package org.taskhub.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.taskhub.projects.Project;
import org.taskhub.projects.ProjectRepository;
import org.taskhub.users.User;
import org.taskhub.users.UserRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    
    public Task getTaskById(Long id) {return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task nicht gefunden mit der ID: " + id)) ;}

    public List<Task> getAllTasks() {return taskRepository.findAll();}

    public Task createTask(Task createdTask) {return taskRepository.save(createdTask);}

    public Task updateTaskProgress(Long taskId, TaskProgress newProgress)
    {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task nicht gefunden mit ID: " + taskId));

        task.setProgress(newProgress);
        return taskRepository.save(task);
    }

    public void deleteTask(Long id)
    {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task nicht gefunden mit ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    public Task assignUserToTask(Long taskId, Long userId)
    {

        User responsibleUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden mit der ID: " + userId));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task nicht gefunden mit der ID: " + taskId));

        task.setResponsibleUser(responsibleUser);
        if (task.getProgress() == TaskProgress.UNASSIGNED)
        {
            task.setProgress(TaskProgress.ASSIGNED);
        }
        return taskRepository.save(task);
    }

    public Task removeUserFromTask(Long taskId)
    {
        return taskRepository.findById(taskId).map(task -> {
            task.setResponsibleUser(null);
            if (task.getProgress() == TaskProgress.ASSIGNED)
            {
                task.setProgress(TaskProgress.UNASSIGNED);
            }
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task nicht  gefunden mit der ID: " + taskId));
    }

    public Task addTaskToProject(Long taskId, Long projectId)
    {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projekt nicht gefunden mit der ID: " + projectId));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task nicht gefunden mit der ID: " + taskId));

        task.setProject(project);
        return taskRepository.save(task);
    }

    public Task removeTaskFromProject(Long taskId)
    {
        return taskRepository.findById(taskId).map(task -> {
            task.setProject(null);
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task nicht gefunden mit der ID: " + taskId));
    }
}

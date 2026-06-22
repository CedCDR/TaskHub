package org.taskhub.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.taskhub.users.User;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    //private final ProjectRepository projectRepository;
    
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

    public boolean deleteTask(Long id)  {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }

    public Task assignUserToTask(Long taskId, User responsibleUser)
    {
        return taskRepository.findById(taskId).map(task -> {
            task.setResponsibleUser(responsibleUser);

            if (task.getProgress() == TaskProgress.UNASSIGNED)
            {
                task.setProgress(TaskProgress.ASSIGNED);
            }
        return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task nicht gefunden mit der ID: " + taskId));
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
}

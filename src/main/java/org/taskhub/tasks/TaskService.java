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
        return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task nicht gefunden mit der ID: " + taskId));
    }

    public Task removeUserFromTask(Long taskId)
    {
        return taskRepository.findById(taskId).map(task -> {
            task.setResponsibleUser(null);
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task nicht  gefunden mit der ID: " + taskId));
    }
}

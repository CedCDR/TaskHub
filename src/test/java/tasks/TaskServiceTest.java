package tasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.taskhub.projects.Project;
import org.taskhub.projects.ProjectRepository;
import org.taskhub.tasks.Task;
import org.taskhub.tasks.TaskProgress;
import org.taskhub.tasks.TaskRepository;
import org.taskhub.tasks.TaskService;
import org.taskhub.users.User;
import org.taskhub.users.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    // =============================
    // Tests für getTaskById()
    // =============================
    @Test
    void getTaskById_ReturnsTask_WhenTaskExists()
    {
        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));

        Task result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("Aufgabe1", result.getName());
    }

    @Test
    void getTaskById_ThrowsException_WhenTaskDoesntExist()
    {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(99L);
        });

        assertTrue(exception.getMessage().contains("Task nicht gefunden"));
    }

    // =============================
    // Tests für createTask()
    // =============================

    @Test
    void createTask_CreatesAndReturnsUnassignedTask()
    {
        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");

        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);

        Task result = taskService.createTask(dummyTask);

        verify(taskRepository, times(1)).save(dummyTask);
        assertNotNull(result);
        assertEquals(TaskProgress.UNASSIGNED, result.getProgress());
        assertEquals("Aufgabe1", result.getName());
    }

    // =============================
    // Tests für updateTaskProgress()
    // =============================

    @Test
    void updateTaskProgress_updatesProgress()
    {
        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");
        dummyTask.setProgress(TaskProgress.UNASSIGNED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);

        Task result = taskService.updateTaskProgress(1L, TaskProgress.IN_PROGRESS);

        assertEquals(TaskProgress.IN_PROGRESS, result.getProgress());
    }


    // =============================
    // Tests für deleteTask()
    // =============================

    @Test
    void deleteTask_DeletesTaskOnce_WhenTaskExists()
    {
        when(taskRepository.existsById(1L)).thenReturn(true);
        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_ThrowsException_WhenTaskDoesntExist()
    {
        when(taskRepository.existsById(99L)).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.deleteTask(99L);
        });

        assertTrue(exception.getMessage().contains("Task nicht gefunden"));
        verify(taskRepository, times(0)).deleteById(99L);
    }

    // =============================
    // Tests für assignUserToTask()
    // =============================

    @Test
    void assignUserToTask_SetsResponsibleUser()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));

        Task result = taskService.assignUserToTask(1L, 1L);

        assertEquals(result.getResponsibleUser(), dummyUser);
    }

    @Test
    void assignUserToTask_ThrowsException_WhenTaskDoesntExist()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));

        RuntimeException exception =  assertThrows(RuntimeException.class, () -> {
            taskService.assignUserToTask(99L, 1L);
        });

        assertTrue(exception.getMessage().contains("Task nicht gefunden"));
    }

    @Test
    void assignUserToTask_ThrowsException_WhenUserDoesntExist()
    {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception =  assertThrows(RuntimeException.class, () -> {
            taskService.assignUserToTask(1L, 99L);
        });
        assertTrue(exception.getMessage().contains("User nicht gefunden"));
    }

    @Test
    void assignUserToTask_SetsNewUser_IfResponsibleUserIsSet()
    {
        User responsibleUser = new User();
        responsibleUser.setId(1L);
        responsibleUser.setFirstName("Cedric");

        User dummyUser = new User();
        dummyUser.setId(2L);
        dummyUser.setFirstName("Alice");

        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");
        dummyTask.setResponsibleUser(responsibleUser);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);
        when(userRepository.findById(2L)).thenReturn(Optional.of(dummyUser));


        Task result = taskService.assignUserToTask(1L, 2L);

        assertEquals(result.getResponsibleUser(), dummyUser);
        assertNotEquals(result.getResponsibleUser(), responsibleUser);
    }

    @Test
    void assignUserToTask_SetsProgressTo_ASSIGNED() {
        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");

        assertEquals(TaskProgress.UNASSIGNED, dummyTask.getProgress());

        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));


        Task result = taskService.assignUserToTask(1L, 1L);

        assertEquals(TaskProgress.ASSIGNED, result.getProgress());
    }

    // ==================================
    // Tests für removeUserFromTask()
    // ==================================

    @Test
    void removeUserFromTask_RemovesResponsibleUser()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");
        dummyTask.setResponsibleUser(dummyUser);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);

        Task result = taskService.removeUserFromTask(1L);

        assertNull(result.getResponsibleUser());
    }
    // ==================================
    // Tests für addTaskToProject()
    // ==================================
    @Test
    void addTaskToProject_SetsProject()
    {
        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));

        Task result  = taskService.addTaskToProject(1L, 1L);

        assertEquals(dummyProject, result.getProject());
    }

    @Test
    void addTaskToProject_SetsProject_IfProjectIsSet()
    {
        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        Project existingProject = new Project();
        existingProject.setId(2L);
        existingProject.setName("Enterprise");

        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");
        dummyTask.setProject(existingProject);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));

        Task result  = taskService.addTaskToProject(1L, 1L);

        assertEquals(dummyProject, result.getProject());
    }

    @Test
    void addTaskToProject_ThrowsException_IfTaskDoesntExist()
    {
        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.addTaskToProject(99L, 1L);
        });

        assertTrue(exception.getMessage().contains("Task nicht gefunden"));
    }

    @Test
    void addTaskToProject_ThrowsException_IfProjectDoesntExist()
    {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception  = assertThrows(RuntimeException.class, () -> {
            taskService.addTaskToProject(1L,  99L);
        });

        assertTrue(exception.getMessage().contains("Projekt nicht gefunden"));
    }

    // =============================
    // Tests für removeTaskFromProject()
    // =============================

    @Test
    void removeTaskFromProject_RemovesProject()
    {
        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");
        dummyTask.setProject(dummyProject);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);

        Task result = taskService.removeTaskFromProject(1L);

        assertNull(result.getProject());
    }
}

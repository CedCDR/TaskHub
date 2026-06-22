import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.taskhub.tasks.Task;
import org.taskhub.tasks.TaskProgress;
import org.taskhub.tasks.TaskRepository;
import org.taskhub.tasks.TaskService;
import org.taskhub.users.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

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
    void createTask_CreatesAndReturnsTask()
    {
        Task dummyTask = new Task();
        dummyTask.setId(1L);
        dummyTask.setName("Aufgabe1");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));

        Task result = taskService.createTask(dummyTask);

        verify(taskRepository, times(1)).save(dummyTask);
        assertNotNull(result);
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

        Task result = taskService.updateTaskProgress(1L, TaskProgress.IN_PROGRESS);

        assertEquals(TaskProgress.IN_PROGRESS, result.getProgress());
    }


    // =============================
    // Tests für deleteTask()
    // =============================

    @Test
    void deleteTaskById_ReturnsTrueAndDeletes_WhenTaskExists()
    {
        when(taskRepository.existsById(1L)).thenReturn(true);
        boolean result = taskService.deleteTask(1L);

        assertTrue(result);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTaskById_ThrowsException_WhenTaskDoesntExist()
    {
        when(taskRepository.existsById(99L)).thenReturn(false);
        boolean result = taskService.deleteTask(99L);

        assertFalse(result);
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

        Task result = taskService.assignUserToTask(1L, dummyUser);

        assertEquals(result.getResponsibleUser(), dummyUser);
    }

    @Test
    void assignUserToTask_DoesNothing_WhenUserDoesntExist()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception =  assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(99L);
        });

        assertTrue(exception.getMessage().contains("Task nicht gefunden"));
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

        when(taskRepository.findById(1L)).thenReturn(Optional.of(dummyTask));
        when(taskRepository.save(dummyTask)).thenReturn(dummyTask);

        Task result = taskService.assignUserToTask(1L, dummyUser);

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

        Task result = taskService.assignUserToTask(1L, dummyUser);

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
}

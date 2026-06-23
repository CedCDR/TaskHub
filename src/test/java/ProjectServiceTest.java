import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.taskhub.projects.Project;
import org.taskhub.projects.ProjectProgress;
import org.taskhub.projects.ProjectRepository;
import org.taskhub.projects.ProjectService;
import org.taskhub.users.User;
import org.taskhub.users.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    // =============================
    // Tests für getProjectById()
    // =============================
    @Test
    void getProjectById_ReturnsProject_WhenProjectExists()
    {
        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));

        Project result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals("TaskHub", result.getName());
    }

    @Test
    void getProjectById_ThrowsException_WhenProjectDoesntExist()
    {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.getProjectById(99L);
        });

        assertTrue(exception.getMessage().contains("Projekt nicht gefunden"));
    }

    // =============================
    // Tests für createProject()
    // =============================

    @Test
    void createProject_CreatesAndReturnsActiveProject()
    {
        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        when(projectRepository.save(dummyProject)).thenReturn(dummyProject);

        Project result = projectService.createProject(dummyProject);

        verify(projectRepository, times(1)).save(dummyProject);
        assertNotNull(result);
        assertEquals(ProjectProgress.ACTIVE, result.getProgress());
        assertEquals("TaskHub", result.getName());
    }

    // =============================
    // Tests für updateProjectProgress()
    // =============================

    @Test
    void updateProjectProgress_updatesProgress()
    {
        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");
        dummyProject.setProgress(ProjectProgress.ACTIVE);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));
        when(projectRepository.save(dummyProject)).thenReturn(dummyProject);

        Project result = projectService.updateProjectProgress(1L, ProjectProgress.COMPLETED);

        assertEquals(ProjectProgress.COMPLETED, result.getProgress());
    }

    // =============================
    // Tests für deleteProject()
    // =============================

    @Test
    void deleteProject_DeletesProjectOnce_WhenProjectExists()
    {
        when(projectRepository.existsById(1L)).thenReturn(true);
        projectService.deleteProject(1L);

        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProject_ThrowsException_WhenProjectDoesntExist()
    {
        when(projectRepository.existsById(99L)).thenReturn((false));
        RuntimeException exception = assertThrows(RuntimeException.class, () ->  {
            projectService.deleteProject(99L);
        });

        assertTrue(exception.getMessage().contains("Projekt nicht gefunden"));
        verify(projectRepository, times(0)).deleteById(99L);
    }

    // =============================
    // Tests für assignLeadToProject()
    // =============================

    @Test
    void assignLeadToProject_SetsProjectLead()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));
        when(projectRepository.save(dummyProject)).thenReturn(dummyProject);
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));


        Project result = projectService.assignLeadToProject(1L, 1L);

        assertEquals(dummyUser, result.getProjectLead());
    }

    @Test
    void assignLeadToProject_ThrowsException_WhenProjectDoesntExist()
    {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.assignLeadToProject(99L, 1L);
        });

        assertTrue(exception.getMessage().contains("Projekt nicht gefunden"));
    }

    @Test
    void assignLeadToProject_ThrowsException_WhenUserDoesntExist()
    {

        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.assignLeadToProject(1L, 99L);
        });

        assertTrue(exception.getMessage().contains("User nicht gefunden"));
    }

    @Test
    void assignLeadToProject_SetsNewUser_IfProjectLeadIsSet()
    {
        User projectLead = new User();
        projectLead.setId(1L);
        projectLead.setFirstName("Cedric");

        User dummyUser = new User();
        dummyUser.setId(2L);
        dummyUser.setFirstName("Alice");

        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");
        dummyProject.setProjectLead(projectLead);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));
        when(projectRepository.save(dummyProject)).thenReturn(dummyProject);
        when(userRepository.findById(2L)).thenReturn(Optional.of(dummyUser));

        Project result = projectService.assignLeadToProject(1L, 2L);

        assertEquals(result.getProjectLead(),  dummyUser);
        assertNotEquals(result.getProjectLead(), projectLead);
    }

    // ==================================
    // Tests für removeUserFromTask()
    // ==================================

    @Test
    void  removeLeadFromProject_RemovesProjectLead()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        Project dummyProject  = new Project();
        dummyProject.setId(1L);
        dummyProject.setName("TaskHub");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(dummyProject));
        when(projectRepository.save(dummyProject)).thenReturn(dummyProject);

        Project result = projectService.removeLeadFromProject(1L);

        assertNull(result.getProjectLead());
    }
}

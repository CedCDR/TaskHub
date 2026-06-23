package org.taskhub.projects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.taskhub.users.User;
import org.taskhub.users.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException
                ("Projekt nicht gefunden mit der ID: " + projectId));
        }

        public List<Project> getAllTasks()
        {
            return projectRepository.findAll();
        }

        public Project createProject(Project createdproject)
        {
            return projectRepository.save(createdproject);
        }

        public void deleteProject(Long projectId)
        {
            if (!projectRepository.existsById(projectId))
            {
                throw new RuntimeException("Projekt nicht gefunden mit der ID: " + projectId);
            }
            projectRepository.deleteById(projectId);
        }

    public Project updateProjectProgress(Long projectId, ProjectProgress newProgress) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projekt nicht gefunden mit der ID: " + projectId));

        project.setProgress(newProgress);
        if (project.getProgress() == ProjectProgress.ACTIVE)
        {
            project.setIsActive(true);
        } else {
            project.setIsActive(false);
        }
        return projectRepository.save(project);
    }

        public Project assignLeadToProject(Long projectId, Long userId)
        {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Projekt nicht gefunden mit ID: " + projectId));

            User projectLead = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User nicht gefunden mit ID: " + userId));

            project.setProjectLead(projectLead);
            return projectRepository.save(project);
        }

        public Project removeLeadFromProject(Long projectId)
        {
            return projectRepository.findById(projectId).map(project -> {
                project.setProjectLead(null);
                return projectRepository.save(project);
            }).orElseThrow(() -> new RuntimeException("Projekt nicht gefunden mit der ID: " + projectId));
        }
}


package com.example.taskmanager;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.dto.UpdateTaskStatusRequest;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskServiceImpl;
import com.example.taskmanager.status.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for TaskService.
 */
@DataJpaTest
@Import(TaskServiceImpl.class)
@ActiveProfiles("test")
class TaskServiceTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_shouldCreateTaskWithAllFields() {
        // Given
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        CreateTaskRequest request = new CreateTaskRequest(
                "Test Task",
                "Test Description",
                TaskStatus.TO_DO,
                dueDate
        );

        // When
        TaskDTO result = taskService.createTask(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(result.getDueDate()).isEqualTo(dueDate);
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void createTask_shouldCreateTaskWithNullDescription() {
        // Given
        CreateTaskRequest request = new CreateTaskRequest(
                "Task without description",
                null,
                TaskStatus.IN_PROGRESS,
                null
        );

        // When
        TaskDTO result = taskService.createTask(request);

        // Then
        assertThat(result.getDescription()).isNull();
        assertThat(result.getDueDate()).isNull();
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        // Given
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.TO_DO, null);
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.DONE, LocalDateTime.now());
        taskRepository.save(task1);
        taskRepository.save(task2);

        // When
        List<TaskDTO> tasks = taskService.getAllTasks();

        // Then
        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(TaskDTO::getTitle).containsExactlyInAnyOrder("Task 1", "Task 2");
    }

    @Test
    void getAllTasks_shouldReturnEmptyListWhenNoTasks() {
        // When
        List<TaskDTO> tasks = taskService.getAllTasks();

        // Then
        assertThat(tasks).isEmpty();
    }

    @Test
    void getTaskById_shouldReturnTaskWhenExists() {
        // Given
        Task task = new Task("Test", "Desc", TaskStatus.IN_PROGRESS, null);
        taskRepository.save(task);
        Long id = task.getId();

        // When
        TaskDTO result = taskService.getTaskById(id);

        // Then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getTitle()).isEqualTo("Test");
    }

    @Test
    void getTaskById_shouldThrowExceptionWhenNotFound() {
        // Given
        Long nonExistentId = 999L;

        // Then
        assertThatThrownBy(() -> taskService.getTaskById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void updateTaskStatus_shouldUpdateStatus() {
        // Given
        Task task = new Task("Test", "Desc", TaskStatus.TO_DO, null);
        taskRepository.save(task);
        Long id = task.getId();

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        // When
        TaskDTO result = taskService.updateTaskStatus(id, request);

        // Then
        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateTaskStatus_shouldThrowExceptionWhenTaskNotFound() {
        // Given
        Long nonExistentId = 999L;
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        // Then
        assertThatThrownBy(() -> taskService.updateTaskStatus(nonExistentId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void deleteTask_shouldRemoveTask() {
        // Given
        Task task = new Task("To Delete", "Desc", TaskStatus.TO_DO, null);
        taskRepository.save(task);
        Long id = task.getId();

        // When
        taskService.deleteTask(id);

        // Then
        assertThat(taskRepository.findById(id)).isEmpty();
    }

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskNotFound() {
        // Given
        Long nonExistentId = 999L;

        // Then
        assertThatThrownBy(() -> taskService.deleteTask(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found");
    }
}

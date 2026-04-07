package com.example.taskmanager;

import com.example.taskmanager.controller.TaskController;
import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.PaginatedResponse;
import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.dto.UpdateTaskStatusRequest;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.status.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests using MockMvc with mocked service layer.
 */
//@SpringBootTest
//@AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        // Mocks are managed by Mockito and reset by Spring
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        // Given
        LocalDateTime dueDate = LocalDateTime.now().plusDays(3);
        CreateTaskRequest request = new CreateTaskRequest(
                "New Task",
                "Task Description",
                TaskStatus.TO_DO,
                dueDate
        );

        TaskDTO createdTask = new TaskDTO(
                1L,
                "New Task",
                "Task Description",
                TaskStatus.TO_DO,
                dueDate,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(createdTask);

        // When & Then
        mockMvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task Description"))
                .andExpect(jsonPath("$.status").value("TO_DO"))
                .andExpect(jsonPath("$.dueDate").exists());
    }

    @Test
    void createTask_shouldReturnBadRequestWhenInvalid() throws Exception {
        // Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle(""); // Empty title should fail validation

        // When & Then
        mockMvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() throws Exception {
        // Given
        TaskDTO task1 = new TaskDTO(1L, "Task 1", "Desc 1", TaskStatus.TO_DO, null,
                LocalDateTime.now(), LocalDateTime.now());
        TaskDTO task2 = new TaskDTO(2L, "Task 2", "Desc 2", TaskStatus.DONE, LocalDateTime.now(),
                LocalDateTime.now(), LocalDateTime.now());

        PaginatedResponse<TaskDTO> paginatedResponse = new PaginatedResponse<>(
                List.of(task1, task2), 0, 20, 2
        );

        when(taskService.getTasks(0, 20)).thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/v1/tasks")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].title").value("Task 1"))
                .andExpect(jsonPath("$.data[1].title").value("Task 2"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        // Given
        TaskDTO task = new TaskDTO(1L, "Test", "Desc", TaskStatus.IN_PROGRESS, null,
                LocalDateTime.now(), LocalDateTime.now());
        when(taskService.getTaskById(1L)).thenReturn(task);

        // When & Then
        mockMvc.perform(get("/v1/tasks/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void getTaskById_shouldReturnNotFoundWhenMissing() throws Exception {
        // Given
        when(taskService.getTaskById(999L))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/v1/tasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
    }

    @Test
    void updateTaskStatus_shouldReturnUpdatedTask() throws Exception {
        // Given
        TaskDTO updatedTask = new TaskDTO(1L, "Test", "Desc", TaskStatus.DONE, null,
                LocalDateTime.now(), LocalDateTime.now());
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);
        when(taskService.updateTaskStatus(eq(1L), any(UpdateTaskStatusRequest.class))).thenReturn(updatedTask);

        // When & Then
        mockMvc.perform(patch("/v1/tasks/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void updateTaskStatus_shouldReturnNotFoundWhenMissing() throws Exception {
        // Given
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);
        when(taskService.updateTaskStatus(eq(999L), any(UpdateTaskStatusRequest.class)))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

        // When & Then
        mockMvc.perform(patch("/v1/tasks/{id}/status", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
    }

    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        // Given - service does nothing (no exception)

        // When & Then
        mockMvc.perform(delete("/v1/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_shouldReturnNotFoundWhenMissing() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Task not found with id: 999"))
                .when(taskService).deleteTask(999L);

        // When & Then
        mockMvc.perform(delete("/v1/tasks/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void endpoints_shouldHandleMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                .andExpect(status().isBadRequest());
    }
}

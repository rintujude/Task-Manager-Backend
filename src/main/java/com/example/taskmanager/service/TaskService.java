package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.PaginatedResponse;
import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.dto.UpdateTaskStatusRequest;
import com.example.taskmanager.model.Task;

import java.util.List;

/**
 * Service interface for Task business logic.
 */
public interface TaskService {

    /**
     * Creates a new task.
     *
     * @param request the task creation request
     * @return the created task DTO
     */
    TaskDTO createTask(CreateTaskRequest request);

    /**
     * Retrieves all tasks (deprecated - use getTasksWithPagination instead).
     *
     * @return list of all task DTOs
     */
    @Deprecated
    List<TaskDTO> getAllTasks();

    /**
     * Retrieves tasks with pagination.
     *
     * @param page zero-based page index
     * @param size number of tasks per page
     * @return PaginatedResponse containing task DTOs and pagination metadata
     */
    PaginatedResponse<TaskDTO> getTasks(int page, int size);

    /**
     * Retrieves a task by its ID.
     *
     * @param id the task ID
     * @return the task DTO
     * @throws IllegalArgumentException if task not found
     */
    TaskDTO getTaskById(Long id);

    /**
     * Updates the status of a task.
     *
     * @param id the task ID
     * @param request the status update request
     * @return the updated task DTO
     * @throws IllegalArgumentException if task not found
     */
    TaskDTO updateTaskStatus(Long id, UpdateTaskStatusRequest request);

    /**
     * Deletes a task.
     *
     * @param id the task ID
     * @throws IllegalArgumentException if task not found
     */
    void deleteTask(Long id);
}

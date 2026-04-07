package com.example.taskmanager.dto;

import com.example.taskmanager.status.TaskStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating task status.
 */
public class UpdateTaskStatusRequest {

    @NotNull(message = "Status is required")
    private TaskStatus status;

    // Constructors
    public UpdateTaskStatusRequest() {
    }

    public UpdateTaskStatusRequest(TaskStatus status) {
        this.status = status;
    }

    // Getters and Setters
    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}

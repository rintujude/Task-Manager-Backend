package com.example.taskmanager.dto;

import com.example.taskmanager.status.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating task status.
 */

@Data
@NoArgsConstructor
public class UpdateTaskStatusRequest {

    @NotNull(message = "Status is required")
    private TaskStatus status;

    public UpdateTaskStatusRequest(TaskStatus status) {
        this.status = status;
    }
}

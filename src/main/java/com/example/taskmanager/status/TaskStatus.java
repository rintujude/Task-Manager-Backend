package com.example.taskmanager.status;

/**
 * Enum representing the possible statuses of a task.
 * <p>
 * TO_DO: Task is pending and not yet started
 * IN_PROGRESS: Task is currently being worked on
 * DONE: Task has been completed
 */
public enum TaskStatus {
    TO_DO,
    IN_PROGRESS,
    DONE;

    /**
     * Validates if the given string is a valid TaskStatus.
     *
     * @param status the status string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String status) {
        try {
            TaskStatus.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}

package com.example.application.taskmanagement.service;

import com.example.application.taskmanagement.domain.Task;
import com.example.application.taskmanagement.domain.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TaskService {

    private final TaskRepository taskRepository;
    private final Clock clock;

    public TaskService(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    public void createTask(String description, @Nullable LocalDate dueDate) {
        if ("fail".equals(description)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var task = new Task();
        task.setDescription(description);
        task.setCreationDate(clock.instant());
        task.setDueDate(dueDate);
        taskRepository.saveAndFlush(task);
    }

    public void updateTask(Task task) {
        taskRepository.saveAndFlush(task);
    }

    public List<Task> list(Pageable pageable) {
        return taskRepository.findAllBy(pageable).toList();
    }

    public void completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
        task.setCompleted(true);
        taskRepository.saveAndFlush(task);
    }

    public void uncompleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
        task.setCompleted(false);
        taskRepository.saveAndFlush(task);
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Task not found with id: " + taskId);
        }
        taskRepository.deleteById(taskId);
        taskRepository.flush();
    }

    public List<Task> listByCompletionStatus(boolean completed, Pageable pageable) {
        return taskRepository.findByCompleted(completed, pageable).toList();
    }
}
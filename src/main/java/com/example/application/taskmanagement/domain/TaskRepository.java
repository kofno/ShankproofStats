package com.example.application.taskmanagement.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    // Find tasks by completion status
    Slice<Task> findByCompleted(boolean completed, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.completed = :completed WHERE t.id = :id")
    void updateTaskCompletion(Long id, boolean completed);


    // If you don't need a total row count, Slice is better than Page.
    Slice<Task> findAllBy(Pageable pageable);
}

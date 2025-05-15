package com.example.application.taskmanagement.ui.view;

import com.example.application.base.ui.component.ViewToolbar;
import com.example.application.taskmanagement.domain.Task;
import com.example.application.taskmanagement.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("task-list")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
@PermitAll
public class TaskListView extends Main {

    private final TaskService taskService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;

    public TaskListView(TaskService taskService, Clock clock) {
        this.taskService = taskService;

        // Existing initialization code...
        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(clock.getZone())
                .withLocale(getLocale());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(new ComponentRenderer<>(task -> {
            DatePicker datePicker = new DatePicker();
            datePicker.setValue(task.getDueDate());
            datePicker.addValueChangeListener(e -> {
                task.setDueDate(e.getValue());
                taskService.updateTask(task);
                taskGrid.getDataProvider().refreshItem(task);
                showSuccessNotification("Due date updated");
            });
            return datePicker;
        })).setHeader("Due Date").setWidth("200px");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creation Date");
        
        // Add status column
        taskGrid.addColumn(task -> task.isCompleted() ? "Completed" : "Pending")
                .setHeader("Status");

        // Add actions column
        taskGrid.addColumn(new ComponentRenderer<>(task -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button toggleButton = new Button(new Icon(task.isCompleted() ? 
                    VaadinIcon.ARROW_BACKWARD : VaadinIcon.CHECK));
            toggleButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            toggleButton.addClickListener(e -> handleTaskCompletion(task));
            toggleButton.setTooltipText(task.isCompleted() ? "Mark as incomplete" : "Complete task");

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(task));
            deleteButton.setTooltipText("Delete task");

            actions.add(toggleButton, deleteButton);
            return actions;
        })).setHeader("Actions").setAutoWidth(true);

        taskGrid.setSizeFull();

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(new ViewToolbar("Task List", ViewToolbar.group(description, dueDate, createBtn)));
        add(taskGrid);
    }

    private void handleTaskCompletion(Task task) {
        if (task.isCompleted()) {
            taskService.uncompleteTask(task.getId());
            task.setCompleted(false); // Update the task in the grid
            showSuccessNotification("Task marked as incomplete");
        } else {
            taskService.completeTask(task.getId());
            task.setCompleted(true); // Update the task in the grid
            showSuccessNotification("Task completed");
        }
        taskGrid.getDataProvider().refreshItem(task);
    }

    private void confirmDelete(Task task) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Task");
        dialog.setText("Are you sure you want to delete this task? This action cannot be undone.");
        
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setCancelText("Cancel");
        dialog.setConfirmButtonTheme("error primary");
        
        dialog.addConfirmListener(event -> {
            taskService.deleteTask(task.getId());
            taskGrid.getDataProvider().refreshAll();
            showSuccessNotification("Task deleted");
        });
        
        dialog.open();
    }

    private void showSuccessNotification(String message) {
        Notification.show(message, 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void createTask() {
        taskService.createTask(description.getValue(), dueDate.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        showSuccessNotification("Task added");
    }
}
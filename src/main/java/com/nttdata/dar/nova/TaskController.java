package com.nttdata.dar.nova;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TaskController {
	
	@Autowired
	private final TaskRepository taskRepository;
	
	private TaskController (TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	@GetMapping("/tasks")
	public List<Task> getTasks(){
		return (List<Task>) this.taskRepository.findAll();
	}
	
	@GetMapping("/pendingTasks")
	public List<Task> getPendingTasks(){		
		return this.taskRepository.findByStatusIs("Pending");
	}
	
	@GetMapping("/completedTasks")
	public List<Task> getCompletedTasks(){		
		return this.taskRepository.findByStatusIs("Completed");
	}
	
	@GetMapping("/taskById")
	public Optional<Task> getTaskById(@RequestParam Long id) {
		return this.taskRepository.findById(id);
	}
	
	@PostMapping("/tasks")
	public Task addTask(@RequestBody Task task) {
		return this.taskRepository.save(task);
	}
	
	@PutMapping("/tasks")
	public Task updateTask(@RequestBody Task task) {
		return this.taskRepository.save(task);
	}
	
	@DeleteMapping("/tasks")
	public void deleteTaskById(@RequestParam Long id) {
		this.taskRepository.deleteById(id);
	}
	
	@DeleteMapping("/allTasks")
	public void deteleAllTasks() {
		this.taskRepository.deleteAll();
	}
}

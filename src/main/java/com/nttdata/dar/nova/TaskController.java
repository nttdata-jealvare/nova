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

/**
 * NOVA Project TaskController
 * 
 * @author jalvarco
 */
@RestController
public class TaskController {

	/**
	 * External object communicating directly with the database
	 */
	@Autowired
	private TaskRepository taskRepository;

	/**
	 * Listing of all tasks from the system
	 * 
	 * @return List<Tasks> or Empty JSONObject
	 */
	@GetMapping("/tasks")
	public List<Task> getTasks() {
		return (List<Task>) this.taskRepository.findAll();
	}

	/**
	 * Listing of all pending tasks from the system
	 * 
	 * @return List<Tasks> or Empty JSONObject
	 */
	@GetMapping("/pendingTasks")
	public List<Task> getPendingTasks() {
		return this.taskRepository.findByStatusIs("Pending");
	}

	/**
	 * Listing of all completed tasks from the system
	 * 
	 * @return List<Task> or Empty JSONObject
	 */
	@GetMapping("/completedTasks")
	public List<Task> getCompletedTasks() {
		return this.taskRepository.findByStatusIs("Completed");
	}

	/**
	 * Get a task by ID
	 * 
	 * @param id
	 * @return Task or Null
	 */
	@GetMapping("/taskById")
	public Optional<Task> getTaskById(@RequestParam Long id) {
		return this.taskRepository.findById(id);
	}

	/**
	 * Add a new Task
	 * 
	 * @param task
	 * @return Task
	 */
	@PostMapping("/tasks")
	public Task addTask(@RequestBody Task task) {
		return this.taskRepository.save(task);
	}

	/**
	 * Update a concrete Task
	 * 
	 * @param task
	 * @return task
	 */
	@PutMapping("/tasks")
	public Task updateTask(@RequestBody Task task) {
		return this.taskRepository.save(task);
	}

	/**
	 * Deletion of one concrete task
	 * 
	 * @param id
	 */
	@DeleteMapping("/tasks")
	public void deleteTaskById(@RequestParam Long id) {
		this.taskRepository.deleteById(id);
	}

	/**
	 * Deletion of all existing tasks
	 */
	@DeleteMapping("/allTasks")
	public void deteleAllTasks() {
		this.taskRepository.deleteAll();
	}
}

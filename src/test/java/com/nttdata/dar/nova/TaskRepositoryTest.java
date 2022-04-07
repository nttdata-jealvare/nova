package com.nttdata.dar.nova;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * @author jalvarco
 *
 */
@DataJpaTest
public class TaskRepositoryTest {

	@Autowired
	private TaskRepository taskRepository;
	
	private Task externalTask;
	private Task task;
	
	private final String descriptionT = "Test";
	private final String statusT = "Pending";
	
	private static final Long ID = 1L;
	
	@BeforeEach
	public void initialize() {
		this.task = new Task(this.descriptionT, this.statusT);
		this.externalTask = new Task(this.descriptionT);
	}
	
	
	/**
	 * 
	 */
	@Test
	public void createTask() {		
		Task createdTask = this.taskRepository.save(this.task);
		assertThat(createdTask.getDescription()).isEqualTo(this.task.getDescription());
		assertThat(createdTask.getStatus()).isEqualTo(this.task.getStatus());
	}
	
	@Test
	public void getTasks() {
		// Clean database for control the task list
		this.taskRepository.deleteAll();
		
		String descMod = "Test 2";
		String statusMod = "Completed";
		this.externalTask.setDescription(descMod);
		this.externalTask.setStatus(statusMod);
		
		List<Task> listTasks = new ArrayList<>();
		listTasks.add(this.task);
		listTasks.add(this.externalTask);
		
		this.taskRepository.save(this.task);
		this.taskRepository.save(this.externalTask);
		
		assertThat(this.taskRepository.findAll().size()).isEqualTo(listTasks.size());
	}
	
	// 
	@Test
	public void getPendingTasks() {
		List<Task> pendingTasks = this.taskRepository.findByStatusIs("Pending");
		for(Task t : pendingTasks) {
			assertThat(t.getStatus()).isEqualTo("Pending");
		}
	}
	
	@Test
	public void getCompletedTasks() {
		List<Task> completedTasks = this.taskRepository.findByStatusIs("Completed");
		for(Task t : completedTasks) {
			assertThat(t.getStatus()).isEqualTo("Completed");
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void updateTask() {
		String desc = "New Description"; String stat = "Pending";
		
		//Assign the ID it should have and new values
		this.task.setId(ID);
		this.task.setDescription(desc);
		this.task.setStatus(stat);
		
		// Update Record
		Task updatedTask = this.taskRepository.save(this.task);
		assertThat(updatedTask.getId()).isEqualTo(ID);
		assertThat(updatedTask.getDescription()).isEqualTo(desc);
		assertThat(updatedTask.getStatus()).isEqualTo(stat);
		
	}
	
	/**
	 * 
	 */
	@Test
	public void deleteTask() {
		this.taskRepository.save(this.task);
		assertThat(this.taskRepository.count()).isNotZero();
		
		// Delete task by ID and then check it 
		this.taskRepository.deleteById(ID);
		assertThat(this.taskRepository.existsById(ID)).isNotEqualTo(true);
	}
	
	@Test
	public void deleteAllTasks() {
		this.taskRepository.deleteAll();
		assertThat(this.taskRepository.count()).isZero();
	}
	
	/**
	 * 
	 */
	@Test
	public void newExternalTask() {
		Task newTask = this.taskRepository.save(this.externalTask);
		String newState = "New";
		
		assertThat(newTask.getStatus()).isEqualTo(newState);
	}
	
	
}

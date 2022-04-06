package com.nttdata.dar.nova;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TaskRepositoryTest {

	@Autowired
	private TaskRepository taskRepository;
	
	private Task task;
	private Long id = 1L;
	private String descriptionT = "Test";
	private String statusT = "In Progress";

	
	@BeforeEach
	public void initialize() {
		this.task = new Task(this.descriptionT, this.statusT);
	}
	
	@Test
	public void createTask() {
		Task createdTask = this.taskRepository.save(this.task);
		assertThat(createdTask.getDescription()).isEqualTo(this.task.getDescription());
		assertThat(createdTask.getStatus()).isEqualTo(this.task.getStatus());
	}
	
	@Test
	public void updateTask() {
		String desc = "New Description"; String stat = "Pending";
		
		// Create Task
		this.taskRepository.save(this.task);
		
		//Assign the ID it should have and new values
		this.task.setId(this.id);
		this.task.setDescription(desc);
		this.task.setStatus(stat);
		
		// TODO: Hacer esto con assertEquals
		
		// Update Record
		Task updatedTask = this.taskRepository.save(this.task);
		assertThat(updatedTask.getId()).isEqualTo(this.id);
		assertThat(updatedTask.getDescription()).isEqualTo(desc);
		assertThat(updatedTask.getStatus()).isEqualTo(stat);
		
	}
	
}

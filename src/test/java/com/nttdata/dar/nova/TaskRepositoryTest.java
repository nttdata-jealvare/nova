package com.nttdata.dar.nova;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * NOVA Project TaskRespositoryTest
 * 
 * @author jalvarco
 */
@DataJpaTest
public class TaskRepositoryTest {

	/**
	 * External object communicating directly with the database
	 */
	@Autowired
	private TaskRepository taskRepository;

	/**
	 * Auxiliary task applied to simulate external testing
	 */
	private Task externalTask;

	/**
	 * Main task applied to testing
	 */
	private Task task;

	/**
	 * Variables used during test execution to create new tasks
	 */
	private final String descriptionT = "Test";
	private final String statusT = "Pending";
	private static final Long ID = 1L;

	/**
	 * Initialize the tasks for each test
	 */
	@BeforeEach
	public void initialize() {
		this.task = new Task(this.descriptionT, this.statusT);
		this.externalTask = new Task(this.descriptionT);
	}

	/**
	 * Adding a new task
	 */
	@Test
	public void createTask() {
		Task createdTask = this.taskRepository.save(this.task);
		assertThat(createdTask.getDescription()).isEqualTo(this.task.getDescription());
		assertThat(createdTask.getStatus()).isEqualTo(this.task.getStatus());
	}

	/**
	 * Listing of all tasks from the system by database request
	 */
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

		// Saving two tasks to later pull out their information
		this.taskRepository.save(this.task);
		this.taskRepository.save(this.externalTask);

		assertThat(this.taskRepository.findAll().size()).isEqualTo(listTasks.size());
	}

	/**
	 * Extraction of information from an element searched by ID
	 */
	@Test
	public void getATask() {
		Task idTask = new Task(this.descriptionT, this.statusT);

		// Get the information stored in the database
		Task result = this.taskRepository.save(idTask);

		assertThat(this.taskRepository.findById(result.getId()).get().getDescription()).isEqualTo(this.descriptionT);
		assertThat(this.taskRepository.findById(result.getId()).get().getStatus()).isEqualTo(this.statusT);
	}

	/**
	 * List of requests with "Pending" status via database request
	 */
	@Test
	public void getPendingTasks() {
		List<Task> pendingTasks = this.taskRepository.findByStatusIs("Pending");
		for (Task t : pendingTasks) {
			assertThat(t.getStatus()).isEqualTo("Pending");
		}
	}

	/**
	 * List of requests with "Completed" status via database request
	 */
	@Test
	public void getCompletedTasks() {
		List<Task> completedTasks = this.taskRepository.findByStatusIs("Completed");
		for (Task t : completedTasks) {
			assertThat(t.getStatus()).isEqualTo("Completed");
		}
	}

	/**
	 * Updating the content of a single existing task by request to the database
	 */
	@Test
	public void updateTask() {
		String desc = "New Description";
		String stat = "Pending";

		// Assign the ID it should have and new values
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
	 * Deletion of a single existing task via database request
	 */
	@Test
	public void deleteTask() {
		this.taskRepository.save(this.task);
		assertThat(this.taskRepository.count()).isNotZero();

		// Delete task by ID and then check it
		this.taskRepository.deleteById(ID);
		assertThat(this.taskRepository.existsById(ID)).isNotEqualTo(true);
	}

	/**
	 * Deletion of all existing tasks via database request
	 */
	@Test
	public void deleteAllTasks() {
		this.taskRepository.deleteAll();
		assertThat(this.taskRepository.count()).isZero();
	}

	/**
	 * Operation on the database when a task comes with the description only
	 */
	@Test
	public void newExternalTask() {
		Task newTask = this.taskRepository.save(this.externalTask);
		String newState = "New";

		assertThat(newTask.getStatus()).isEqualTo(newState);
	}

}

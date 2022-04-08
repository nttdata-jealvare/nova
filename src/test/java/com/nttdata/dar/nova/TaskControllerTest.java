package com.nttdata.dar.nova;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * NOVA Project TaskControllerTest
 * 
 * @author jalvarco
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

	/**
	 * MockMvc Object to be used for test cases
	 */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * External object used to convert Task objects into JSON objects
	 */
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Main task applied to testing
	 */
	private Task task;

	/**
	 * Variables used during test execution to create new tasks
	 */
	private final String descriptionT = "Test";
	private final String statusT = "In Progress";

	/**
	 * Mapper initializer
	 */
	@TestConfiguration
	static class TestConfigurationApp {
		@Bean
		ObjectMapper objectMapperPrettyPrinting() {
			return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		}
	}

	/**
	 * Clean database and create a new task and then retrieving its ID
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void initialize() throws Exception {

		// Clean database
		RequestBuilder request = MockMvcRequestBuilders.delete("/allTasks").accept(MediaType.APPLICATION_JSON);

		this.mockMvc.perform(request);

		// Create generic task
		this.task = new Task(descriptionT, statusT);

		// Create task avoiding initial load
		MvcResult response = this.mockMvc
				.perform(MockMvcRequestBuilders.post("/tasks").content(objectMapper.writeValueAsBytes(this.task))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andReturn();

		// Set ID for each insertion
		JSONObject jsonResponse = new JSONObject(response.getResponse().getContentAsString());
		this.task.setId(jsonResponse.getLong("id"));
	}

	/**
	 * Adding a new task
	 * 
	 * @throws Exception
	 */
	@Test
	public void createTasks() throws Exception {
		Task newTask = new Task("Create Test");

		// Request
		RequestBuilder request = MockMvcRequestBuilders.post("/tasks").content(objectMapper.writeValueAsBytes(newTask))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		// Assertion
		this.mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$.description", is("Create Test")))
				.andExpect(jsonPath("$.status", is("New")));
	}

	/**
	 * Listing of all requests from the system via API Rest
	 * 
	 * @throws Exception
	 */
	@Test
	public void getTasks() throws Exception {

		// Request
		RequestBuilder request = MockMvcRequestBuilders.get("/tasks").accept(MediaType.APPLICATION_JSON);

		// Assertion
		this.mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(1)));
	}

	/**
	 * Extraction of information from an element searched by ID
	 * 
	 * @throws Exception
	 */
	@Test
	public void getTaskById() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/tasks").param("id", this.task.getId().toString())
				.accept(MediaType.APPLICATION_JSON);

		this.mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(this.task.getId()));
	}

	/**
	 * List of requests with "Pending" status via API Rest
	 * 
	 * @throws Exception
	 */
	@Test
	public void getPendingTasks() throws Exception {

		Task c1 = new Task("Test Completed", "Completed");
		Task p2 = new Task("Test Completed 2", "Pending");
		Task i1 = new Task("Test In Progress", "In Progress");

		ArrayList<Task> tasks = new ArrayList<Task>(Arrays.asList(c1, p2, i1));

		// Create Task List
		for (Task t : tasks) {
			this.mockMvc.perform(MockMvcRequestBuilders.post("/tasks").content(objectMapper.writeValueAsBytes(t))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
		}

		RequestBuilder request = MockMvcRequestBuilders.get("/pendingTasks").accept(MediaType.APPLICATION_JSON);

		MvcResult response = this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(1))).andReturn();

		// Check each row
		JSONArray tasksResponse = new JSONArray(response.getResponse().getContentAsString());

		for (int i = 0; i < tasksResponse.length(); i++) {
			assertEquals("Pending", tasksResponse.getJSONObject(i).getString("status"));
		}
	}

	/**
	 * List of requests with "Completed" status via API Rest
	 * 
	 * @throws Exception
	 */
	@Test
	public void getCompletedTasks() throws Exception {

		Task c1 = new Task("Test Completed", "Completed");
		Task c2 = new Task("Test Completed 2", "Completed");
		Task i1 = new Task("Test In Progress", "In Progress");

		ArrayList<Task> tasks = new ArrayList<Task>(Arrays.asList(c1, c2, i1));

		// Create Task List
		for (Task t : tasks) {
			this.mockMvc.perform(MockMvcRequestBuilders.post("/tasks").content(objectMapper.writeValueAsBytes(t))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
		}

		RequestBuilder request = MockMvcRequestBuilders.get("/completedTasks").accept(MediaType.APPLICATION_JSON);

		MvcResult response = this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(2))).andReturn();

		// Check each row
		JSONArray tasksResponse = new JSONArray(response.getResponse().getContentAsString());

		for (int i = 0; i < tasksResponse.length(); i++) {
			assertEquals("Completed", tasksResponse.getJSONObject(i).getString("status"));
		}

	}

	/**
	 * Updating the content of a single existing task by request via API Rest
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateTask() throws Exception {
		String descU = "UPDATED TEST";
		String statU = "Pending";

		this.task.setDescription(descU);
		this.task.setStatus(statU);

		// Request
		RequestBuilder request = MockMvcRequestBuilders.put("/tasks").content(objectMapper.writeValueAsBytes(this.task))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		// Assertion
		this.mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$.description", is(descU)))
				.andExpect(jsonPath("$.status", is(statU)));
	}

	/**
	 * Deletion of a single existing task via API Rest
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteTask() throws Exception {
		RequestBuilder request, check;

		request = MockMvcRequestBuilders.delete("/tasks").param("id", this.task.getId().toString())
				.accept(MediaType.APPLICATION_JSON);

		// Delete all tasks
		this.mockMvc.perform(request).andExpect(status().isOk());

		// Check for the id
		check = MockMvcRequestBuilders.get("/tasks").param("id", this.task.getId().toString())
				.accept(MediaType.APPLICATION_JSON);

		this.mockMvc.perform(check).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[*].id").doesNotExist());
	}

	/**
	 * Deletion of all existing tasks via API Rest
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteAllTask() throws Exception {

		RequestBuilder request = MockMvcRequestBuilders.delete("/allTasks").accept(MediaType.APPLICATION_JSON);
		// Delete all tasks
		this.mockMvc.perform(request).andExpect(status().isOk()).andExpect(content().string(""));
	}

}

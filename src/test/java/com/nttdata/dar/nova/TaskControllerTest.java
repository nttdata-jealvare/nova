package com.nttdata.dar.nova;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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
 * @author jalvarco
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	private Task task;
	
	private final String descriptionT = "Test";
	private final String statusT = "In Progress";
	
	private static final Long ID = 9L;
	
	/**
	 * 
	 *
	 */
	@TestConfiguration
    static class TestConfigurationApp {
        @Bean
        ObjectMapper objectMapperPrettyPrinting() {
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        }
    }
	
	/**
	 * @throws Exception
	 */
	@BeforeEach
	public void initialize() throws Exception {
		// Clean database
		RequestBuilder request = MockMvcRequestBuilders
				.delete("/allTasks")
				.accept(MediaType.APPLICATION_JSON);
		
		this.mockMvc.perform(request);
		
		// Create generic task
		this.task = new Task(descriptionT, statusT);
		
		// Create task avoiding initial load
		MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
				.content(objectMapper.writeValueAsBytes(this.task))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andReturn();
		
		// Set ID for each insertion
		JSONObject jsonResponse = new JSONObject(response.getResponse().getContentAsString());
		this.task.setId(jsonResponse.getLong("id"));		
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void createTasks() throws Exception{
		Task newTask = new Task("Create Test");
		
		// Request
		RequestBuilder request = MockMvcRequestBuilders
				.post("/tasks")
				.content(objectMapper.writeValueAsBytes(newTask))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		// Assertion
		this.mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.description", is("Create Test")))
		.andExpect(jsonPath("$.status", is("New")));
	}
	
	@Test
	public void getTasks() throws Exception {
		
		// Request
		RequestBuilder request = MockMvcRequestBuilders
				.get("/tasks")
				.accept(MediaType.APPLICATION_JSON);
		
		// Assertion
		this.mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()", is(1)))
		.andReturn();
	}
	
	@Test
	public void getTaskById() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
				.get("/tasks")
				.param("id", this.task.getId().toString())
				.accept(MediaType.APPLICATION_JSON);
		
		this.mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()", is(1)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(this.task.getId()));
	}
	
	@Test
	public void getPendingTasks() throws Exception{
		
		Task c1 = new Task("Test Completed", "Completed");
		Task p2 = new Task("Test Completed 2", "Pending");
		Task i1 = new Task("Test In Progress", "In Progress");
		
		ArrayList<Task> tasks = new ArrayList<Task>(Arrays.asList(c1, p2, i1));
		
		// Create Task List
		for(Task t: tasks) {		
			this.mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
				.content(objectMapper.writeValueAsBytes(t))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		}
		
		RequestBuilder request = MockMvcRequestBuilders
				.get("/pendingTasks")
				.accept(MediaType.APPLICATION_JSON);
		
		MvcResult response = this.mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()", is(1)))
		.andReturn();
		// TODO: completar con el estado
		
		//System.out.println("Salida Pending: \n" + response.getResponse().getContentAsString());
	}
	
	@Test
	public void getCompletedTasks() throws Exception{
		
		Task c1 = new Task("Test Completed", "Completed");
		Task c2 = new Task("Test Completed 2", "Completed");
		Task i1 = new Task("Test In Progress", "In Progress");
		
		ArrayList<Task> tasks = new ArrayList<Task>(Arrays.asList(c1, c2, i1));
		
		// Create Task List
		for(Task t: tasks) {		
			this.mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
				.content(objectMapper.writeValueAsBytes(t))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		}
		
		RequestBuilder request = MockMvcRequestBuilders
				.get("/completedTasks")
				.accept(MediaType.APPLICATION_JSON);
		
		MvcResult response = this.mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()", is(2)))
		.andReturn();
		
		
		JSONObject jsonResponse = new JSONObject(response.getResponse().getContentAsString());
		//JSONArray tasksResponse = jsonResponse.names();
		
		/*for(int i = 0; i < tasksResponse.length(); i++) {
			assertEquals("Completed", tasksResponse.getString("status"));
		}*/
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void updateTask() throws Exception{
		String descU = "UPDATED TEST";
		String statU = "Pending";
		
		this.task.setDescription(descU);
		this.task.setStatus(statU);
		
		// Request
		RequestBuilder request = MockMvcRequestBuilders
				.put("/tasks")
				.content(objectMapper.writeValueAsBytes(this.task))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		// Assertion
		this.mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.description", is(descU)))
		.andExpect(jsonPath("$.status", is(statU)));
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void deleteTask() throws Exception{
		RequestBuilder request, check;
		
		request = MockMvcRequestBuilders
				.delete("/tasks")
				.param("id", this.task.getId().toString())
				.accept(MediaType.APPLICATION_JSON);
    	// Delete all tasks
    	this.mockMvc.perform(request)
    	.andExpect(status().isOk());
    	
    	// Check for the id
    	check = MockMvcRequestBuilders
				.get("/tasks")
				.param("id", this.task.getId().toString())
				.accept(MediaType.APPLICATION_JSON);
		
		this.mockMvc.perform(check)
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[*].id").doesNotExist());
	}
	
    /**
     * @throws Exception
     */
    @Test    
    public void deleteAllTask() throws Exception {
    	
    	RequestBuilder request = MockMvcRequestBuilders
				.delete("/allTasks")
				.accept(MediaType.APPLICATION_JSON);
    	// Delete all tasks
    	this.mockMvc.perform(request)
    	.andExpect(status().isOk())
    	.andExpect(content().string(""));
    }
    	
	
}

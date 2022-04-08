package com.nttdata.dar.nova;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * NOVA Project Task
 * 
 * @author jalvarco
 */
@Entity
public class Task {

	/**
	 * |_ ID: Unique value autoincremented 
	 * |_ Description: String value 
	 * |_ Status: task state
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	@Column(columnDefinition = "varchar(256)")
	private String description;

	@Column(columnDefinition = "varchar(100) default 'New'")
	private String status;

	/**
	 * Default constructor
	 */
	public Task() {

	}

	/**
	 * Constructor to create new tasks with empty state
	 * 
	 * @param description
	 */
	public Task(String description) {
		super();
		this.description = description;
		this.status = "New";
	}

	/**
	 * Constructor to create new task with description and state
	 * 
	 * @param description
	 * @param status
	 */
	public Task(String description, String status) {
		super();
		this.description = description;
		this.status = status;
	}

	/**
	 * Constructor to create new task with all values
	 * 
	 * @param id
	 * @param description
	 * @param status
	 */
	public Task(Long id, String description, String status) {
		super();
		this.id = id;
		this.description = description;
		this.status = status;
	}

	/**
	 * Task ID getter
	 * 
	 * @return Long ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Task ID setter
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Task description getter
	 * 
	 * @return String description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Task description setter
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Task status getter
	 * 
	 * @return String status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Task status setter
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}

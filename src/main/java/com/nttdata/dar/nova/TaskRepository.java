package com.nttdata.dar.nova;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends JpaRepository<Task, Long>{

	/**
	 * Query method that means "status=XXX"
	 * 
	 * @param status
	 * @return query result or null
	 */
	public List<Task> findByStatusIs(String status);
}

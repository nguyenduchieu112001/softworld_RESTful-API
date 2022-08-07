package com.softworld.app1.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softworld.app1.model.Category;

@Repository()
public interface ICategoryReprository extends JpaRepository<Category, Long> {

	@Query(value="select categoryName from Category where categoryID = :c")
	public String getNameById(@Param("c") long categoryId);
	
	@Query(value = "select p.categoryID from Category p")
	public List<Long> getAllCategoryId();
	
	@Query(value = "select * from categories \r\n"
			+ "where id = :c \r\n"
			+ "or name like concat('%', :query, '%') \r\n"
			+ "limit :o,:p", nativeQuery = true)
	public List<Category> getCategoriesWithSearchAndPagination( @Param("c") long categoryId,
															@Param("query") String query, 
															@Param("o") int offset, 
															@Param("p") int pageSize);

}

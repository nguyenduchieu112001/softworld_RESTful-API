package com.softworld.app1.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softworld.app1.model.Category_Post;

@Repository
public interface ICategoryPostRepository extends JpaRepository<Category_Post, Long>{
	
	
	@Query(value = "select category_id from Category_Post where post_id = :c")
	public List<Long> getCategoryIDFromCategoryPost(@Param("c") long postId);
	
	@Transactional
	@Modifying
	@Query(value ="delete from category_posts where category_id = :c and post_id = :p", nativeQuery = true)
	public void delCategoryPost(@Param("c") long categoryID, @Param("p") long postID);
	
	@Transactional
	@Modifying
	@Query(value ="insert into category_posts(category_id, post_id) values (:c, :p)", nativeQuery = true)
	public void insertCategoryPost(@Param("c") long categoryID, @Param("p") long postID);
	
	@Transactional
	@Modifying
	@Query(value = "delete from category_posts where post_id = :p", nativeQuery = true)
	public void delCategoryPostWithPostId(@Param("p") long postID);

}

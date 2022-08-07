package com.softworld.app1.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softworld.app1.model.Post;

@Repository("postRepository")
public interface IPostRepository extends JpaRepository<Post, Long>{
	
	
	//@Modifying
	@Query(value= "select p from Post p where p.postId = :p ")
	public Post getPostById(@Param("p") long  postid);
	
	@Query(value = "select * from posts limit :o, :p", nativeQuery = true)
	public List<Post> getPostsWithPagination(@Param("o") int offset, @Param("p") int pageSize);
	
	@Query(value="select * from posts \r\n"
			+ "where title like concat ('%', :query, '%')\r\n"
			+ "limit :o, :p", nativeQuery = true)
	public List<Post> getPostsWithSearchAndPagination(@Param("query") String query, 
													@Param("o") int offset, 
													@Param("p") int pageSize);
	
	@Query(value = "select distinct p.* \r\n"
			+ "from (posts p join category_posts cp\r\n"
			+ "on p.id = cp.post_id)\r\n"
			+ "join categories c\r\n"
			+ "on cp.category_id = c.id\r\n"
			+ "where p.title like concat('%', :title, '%') \r\n"
			+ "and (c.id = :categoryId or c.name like concat('%', :name, '%'))\r\n"
			+ "limit :o,:p  ", nativeQuery = true)
	public List<Post> getCategoriesAndPostWithSearchAndPagination(@Param("title") String title,
																@Param("categoryId") long categoryId,
																@Param("name") String name,
																@Param("o") int offset,
																@Param("p") int pageSize);
}

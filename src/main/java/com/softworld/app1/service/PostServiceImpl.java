package com.softworld.app1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.softworld.app1.exception.ResourceNotFoundException;
import com.softworld.app1.model.Post;
import com.softworld.app1.repository.IPostRepository;

@Service("postService")
public class PostServiceImpl implements ICommonService<Post> {

	@Autowired
	private IPostRepository postRepository;

	@Override
	public Iterable<Post> findAll() {
		return postRepository.findAll();
	}

	@Override
	public Post save(Post t) {
		return postRepository.save(t);
	}

	public Post update(Post t, long id) {
		return postRepository.save(t);
	}

	@Override
	public Optional<Post> delete(long id) throws Exception {
		postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not available!"));

		postRepository.deleteById(id);
		throw new Exception("Delete completed!");
	}

	@Override
	public Post getById(long id) {
		return postRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post not found by id:" + id));
	}

	public Post getPostById(long postid) {
		return postRepository.getPostById(postid);
	}

	// find Posts with pagination and sort
	public Page<Post> findPostsWithPaginationAndSorting(int offset, int pageSize, String field) {
		Page<Post> p = postRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
		return p;
	}

	// find Posts with pagination
	public Page<Post> findPostsWithPagination(int offset, int pageSize) {
		Page<Post> p = postRepository.findAll(PageRequest.of(offset, pageSize));
		return p;
	}

	public List<Post> getPostsWithPagination(int offset, int pageSize) {
		return postRepository.getPostsWithPagination((offset - 1) * (pageSize), pageSize);
	}

	// find Posts with pagination and search
	public List<Post> getPostsWithSearchAndPagination(String query, int offset, int pageSize) {
		return postRepository.getPostsWithSearchAndPagination(query, (offset - 1) * (pageSize), pageSize);
	}

	// find Categories and Post with pagination and search
	public List<Post> getCategoriesAndPostWithSearchAndPagination(String title, long categoryId, String name,
			int offset, int pageSize) {
		return postRepository.getCategoriesAndPostWithSearchAndPagination(title, categoryId, name,
				(offset - 1) * (pageSize), pageSize);
	}
}

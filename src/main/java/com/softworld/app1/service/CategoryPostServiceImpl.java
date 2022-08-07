package com.softworld.app1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softworld.app1.model.Category_Post;
import com.softworld.app1.repository.ICategoryPostRepository;

@Service
public class CategoryPostServiceImpl implements ICommonService<Category_Post>{

	@Autowired
	private ICategoryPostRepository cpRepository;
	
	@Override
	public Iterable<Category_Post> findAll() {
		return cpRepository.findAll();
	}

	@Override
	public Category_Post save(Category_Post t) {
		return cpRepository.save(t);
	}

	@Override
	public Category_Post update(Category_Post t, long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Category_Post getById(long id) {
		return cpRepository.findById(id).get();
	}

	@Override
	public Optional<Category_Post> delete(long id) throws Exception {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

//	public List<Post> getCategoryPost(long postid) {
//		return cpRepository.getCategoryPost(postid);
//	}
	
	public List<Long> getAll(long postId){
		List<Long> p = new ArrayList<>();
		List<Long> cp = cpRepository.getCategoryIDFromCategoryPost(postId);
		for (Long long1 : cp) {
			p.add(long1);
		}
		return p;
	}

	public void delCategoryPost(long categoryID, long postID) {
		cpRepository.delCategoryPost(categoryID, postID);
	}
	
	public void insertCategoryPost(long categoryID, long postID) {
		cpRepository.insertCategoryPost(categoryID, postID);
	}
	
	public void delCategoryPostWithPostId(long postID) {
		cpRepository.delCategoryPostWithPostId(postID);
	}
}

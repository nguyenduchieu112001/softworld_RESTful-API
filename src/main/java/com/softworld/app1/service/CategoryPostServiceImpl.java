package com.softworld.app1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softworld.app1.model.Category_Post;
import com.softworld.app1.repository.ICategoryPostRepository;

@Service
public class CategoryPostServiceImpl{

	@Autowired
	private ICategoryPostRepository cpRepository;
	

	public Category_Post save(Category_Post t) {
		return cpRepository.save(t);
	}

	public List<Long> getCategoryIDFromCategoryPost(long postId){
		return cpRepository.getCategoryIDFromCategoryPost(postId);
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

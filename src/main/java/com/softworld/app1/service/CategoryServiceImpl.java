package com.softworld.app1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.softworld.app1.exception.ResourceNotFoundException;
import com.softworld.app1.model.Category;
import com.softworld.app1.repository.ICategoryReprository;

@Service()
public class CategoryServiceImpl implements ICommonService<Category> {

	@Autowired
	private ICategoryReprository categoryRepository;
	@Override
	public Iterable<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Category save(Category category) {
		return categoryRepository.save(category);
	}
	
	public Category update(Category t, long id) {
		return categoryRepository.save(t);
	}
	
	public Category getById(long id) {
		return categoryRepository.findById(id).orElseThrow(() -> 
								new ResourceNotFoundException("Cateogry not found by id:" +id));
	}

	public void delete(long id){
		categoryRepository.deleteById(id);
	}
	
	public String getNameById(long categoryId) {
		return categoryRepository.getNameById(categoryId);
	}
	
	public List<Long> getAllCategoryId(){
		return categoryRepository.getAllCategoryId();
	}

	//find Categories with pagination and sort
	public Page<Category> findCategoriesWithPaginationAndSorting(int offset, int pageSize, String field){
		Page<Category> c = categoryRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
		return c;
	}
	
	//find Posts with pagination 
	public Page<Category> findCategoriesWithPagination(int offset, int pageSize){
		Page<Category> c = categoryRepository.findAll(PageRequest.of(offset, pageSize));
		return c;
	}

	//find Categories with pagination and search
	public List<Category> getCategoriesWithSearchAndPagination(long categoryId, String query, int offset, int pageSize){
		return categoryRepository.getCategoriesWithSearchAndPagination(categoryId, query, (offset-1)*(pageSize), pageSize);
	}
}

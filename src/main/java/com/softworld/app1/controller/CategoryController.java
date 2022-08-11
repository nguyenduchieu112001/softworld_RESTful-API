package com.softworld.app1.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softworld.app1.controller.form.DateToSet;
import com.softworld.app1.model.Category;
import com.softworld.app1.service.CategoryServiceImpl;

@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	private CategoryServiceImpl categoryService;

	// Get all Categories
	@RequestMapping(value = "categories", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Category>> getAllCategories(@RequestParam(required = false) String categoryName) {
		try {
			return new ResponseEntity<Iterable<Category>>(categoryService.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Iterable<Category>>(HttpStatus.BAD_REQUEST);
		}
	}

	// get category by Id of category
	@RequestMapping(value = "category/{id}", method = RequestMethod.GET)
	public ResponseEntity<Category> getCategoryById(@PathVariable("id") long id) {
		return new ResponseEntity<Category>(categoryService.getById(id), HttpStatus.OK);
	}

	// create 1 category's data into database categories
	@RequestMapping(value = "category/create", method = RequestMethod.POST)
	public ResponseEntity<Category> addCategory(@Valid @RequestBody Category category) {
		Category cate = new Category();
		try {
			// if create_at and update_at have null value then assign value equal to current
			// time
			DateToSet.setDateOfCreateCategory(category);
			cate = categoryService.save(category);
		} catch (Exception e) {
			e.getMessage();
		}
		return new ResponseEntity<Category>(cate, HttpStatus.OK);
	}

	// update 1 category's data
	@RequestMapping(value = "/category/edit/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Category> updateCategory(@Valid @RequestBody Category categoryForm,
			@PathVariable("id") long id) {
		Category category = categoryService.getById(id);

		category.setCategoryName(categoryForm.getCategoryName());
		DateToSet.setDateOfUpdateCategory(category, categoryForm);

		return new ResponseEntity<Category>(categoryService.save(category), HttpStatus.OK);

	}

	// delete category by Id
	@RequestMapping(value = "/category/delete/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Optional<Category>> deleteCategory(@PathVariable(value = "id") Long id) throws Exception {
		return new ResponseEntity<Optional<Category>>(categoryService.delete(id), HttpStatus.OK);
	}

	// find Categories with Pagination and sort
	@GetMapping("/categories/{offset}/{pagesize}/{field}")
	public ResponseEntity<Page<Category>> findCategoriesWithPaginationAndSorting(@PathVariable("offset") int offset,
			@PathVariable("pagesize") int pageSize, @PathVariable String field) {
		Page<Category> category_page = categoryService.findCategoriesWithPaginationAndSorting(offset, pageSize, field);
		return new ResponseEntity<Page<Category>>(category_page, HttpStatus.OK);
	}

	// find Categories with Pagination and sort
	@GetMapping("/categories/{offset}/{pagesize}")
	public ResponseEntity<Page<Category>> findCategoriesWithPagination(@PathVariable("offset") int offset,
			@PathVariable("pagesize") int pageSize) {
		Page<Category> category_page = categoryService.findCategoriesWithPagination(offset, pageSize);
		return new ResponseEntity<Page<Category>>(category_page, HttpStatus.OK);
	}

	// find Categories with pagination and search
	@GetMapping("/categories/search")
	public ResponseEntity<Page<Category>> getCategoriesWithSearchAndPagination(@RequestParam long categoryId,
			@RequestParam String query, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> pageSize,
			Pageable pageable) {
		List<Category> lisCategory = categoryService.getCategoriesWithSearchAndPagination(categoryId, query,
				page.orElse(1), pageSize.orElse(5));
		Page<Category> category_page = new PageImpl<Category>(lisCategory, pageable, lisCategory.size());
		return new ResponseEntity<Page<Category>>(category_page, HttpStatus.OK);
	}
}

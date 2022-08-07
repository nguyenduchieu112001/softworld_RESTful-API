package com.softworld.app1.service;

import java.util.Optional;

public interface ICommonService<T> {
	
	public Iterable<T> findAll();
	
	public T save(T t);
	
	public T update(T t, long id);

	public T getById(long id);
	
	public Optional<T> delete(long id) throws Exception;

}

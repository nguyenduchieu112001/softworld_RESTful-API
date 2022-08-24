package com.softworld.app1.service;


public interface ICommonService<T> {
	
	public Iterable<T> findAll();
	
	public T save(T t);
	
	public T update(T t, long id);

	public T getById(long id);
	
	public void delete(long id);

}

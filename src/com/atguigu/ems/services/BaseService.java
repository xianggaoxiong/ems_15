package com.atguigu.ems.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.atguigu.ems.daos.BaseDao;
import com.atguigu.ems.entities.Employee;
import com.atguigu.ems.orm.Page;
import com.atguigu.ems.orm.PropertyFilter;

public class BaseService <T>{

	@Autowired
	protected BaseDao<T> dao;
	
	public Page<T> getPage(Page<T> page, Map<String, Object> params) {
		List<PropertyFilter> filters = PropertyFilter.parseParamsToFilters(params);
		page = dao.getPage(page, filters);
		return page;
	}
	
	@Transactional(readOnly = true)
	public Page<T> getPage(Page<T> page) {
		return dao.getPage(page);
	}
	
	@Transactional
	public void save(T entity){
		dao.save(entity);
	}
	
	@Transactional(readOnly=true)
	public List<T> getAll(){
		return dao.getAll();
	}
	
	@Transactional(readOnly=true)
	public T get(Integer id) {
		return dao.get(id);
	}
}

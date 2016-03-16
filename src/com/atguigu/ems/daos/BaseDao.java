package com.atguigu.ems.daos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import com.atguigu.ems.orm.Page;
import com.atguigu.ems.orm.PropertyFilter;
import com.atguigu.ems.orm.PropertyFilter.MatchType;
import com.atguigu.ems.utils.ReflectionUtils;

public class BaseDao<T> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Class<T> entityClass;
	
	public BaseDao() {
		entityClass = ReflectionUtils.getSuperGenericType(getClass());
	}
	
	public Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	public Page<T> getPage(Page<T> page, List<PropertyFilter> filters){
		List<Criterion> criterions = parsePropertyFilterToCriterions(filters);
		
		//1. 查询总的记录数
		long totalElements = getTotalElements(criterions);
		page.setTotalElements(totalElements);
		
		//2. 查询当前页面的 content.
		List<T> content = getContent(page, criterions);
		page.setContent(content);
		
		return page;
	}
	
	private List<T> getContent(Page<T> page, List<Criterion> criterions) {
		//使用 Criteria 完成表连接. 
		Criteria criteria = 
				getSession().createCriteria(entityClass);
		if(criterions != null && criterions.size() > 0){
			for(Criterion criterion: criterions){
				criteria.add(criterion);
			}
		}
		
		//设置分页的参数
		int firstResult = (page.getPageNo() - 1) * page.getPageSize();
		int maxResults = page.getPageSize();
		criteria.setFirstResult(firstResult).setMaxResults(maxResults);
		
		return criteria.list();
	}

	private long getTotalElements(List<Criterion> criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		if(criterions != null && criterions.size() > 0){
			for(Criterion criterion: criterions){
				criteria.add(criterion);
			}
		}
		
		//设置统计查询: 统计 employeeId 的 count.
		criteria.setProjection(Projections.count(getIdName()));
		
		return (long) criteria.uniqueResult();
	}

	private List<Criterion> parsePropertyFilterToCriterions(
			List<PropertyFilter> filters) {
		List<Criterion> criterions = new ArrayList<>();
		
		if(filters != null && filters.size() > 0){
			for(PropertyFilter filter: filters){
				String propertyName = filter.getPropertyName();
				MatchType matchType = filter.getMatchType();
				Class propertyType = filter.getPropertyType();
				Object propertValue = filter.getPropertyVal();
				
				Criterion criterion = null;
				if(propertValue == null || propertValue.toString().trim().equals("")){
					if(MatchType.ISNULL.equals(matchType)){
						criterion = Restrictions.isNull(propertyName);
						criterions.add(criterion);
					}
					continue;
				}
				
				propertValue = ReflectionUtils.convertValue(propertValue, propertyType);
				
				switch(matchType){
				case EQ:
					criterion = Restrictions.eq(propertyName, propertValue);
					break;
				case GE:
					criterion = Restrictions.ge(propertyName, propertValue);
					break;
				case GT:
					criterion = Restrictions.gt(propertyName, propertValue);
					break;
				case LE:
					criterion = Restrictions.le(propertyName, propertValue);
					break;
				case LT:
					criterion = Restrictions.lt(propertyName, propertValue);
					break;
				}
				
				if(criterion != null){
					criterions.add(criterion);
				}
			}
		}
		
		return criterions;
	}

	public void batchSave(List<T> entities) {
		for(int i = 0; i < entities.size(); i++){
			getSession().save(entities.get(i));
			
			if((i + 1) % 50 == 0){
				getSession().flush();
				getSession().clear();
			}
		}
	}
	
	public List<T> getAll() {
		Criteria criteria = getSession().createCriteria(entityClass);
		return criteria.list();
	}
	
	public void evict(T entity){
		getSession().evict(entity);
	}
	
	public void save(T entity){
		getSession().saveOrUpdate(entity);
	}
	
	public T get(Integer id){
		return (T) getSession().get(entityClass, id);
	}
	
	public Page<T> getPage(Page<T> page){
		//1. 查询总的记录数
		long totalElements = getTotalElements();
		page.setTotalElements(totalElements);
		
		//2. 查询当前页面的 content.
		List<T> content = getContent(page);
		page.setContent(content);
		
		return page;
	}
	
	private List<T> getContent(Page<T> page) {
		//使用 Criteria 完成表连接. 
		Criteria criteria = 
				getSession().createCriteria(entityClass);
		
		//设置分页的参数
		int firstResult = (page.getPageNo() - 1) * page.getPageSize();
		int maxResults = page.getPageSize();
		criteria.setFirstResult(firstResult).setMaxResults(maxResults);
		
		return criteria.list();
	}

	private long getTotalElements() {
		Criteria criteria = getSession().createCriteria(entityClass);
		
		//设置统计查询: 统计 employeeId 的 count.
		criteria.setProjection(Projections.count(getIdName()));
		
		return (long) criteria.uniqueResult();
	}
	
	//获取 id 的属性名. 
	public String getIdName(){
		ClassMetadata cmd = this.sessionFactory.getClassMetadata(entityClass);
		return cmd.getIdentifierPropertyName();
	}

	public T getBy(String propertyName, Object propertyValue){
		Criteria criteria = getSession().createCriteria(entityClass);
		Criterion criterion = Restrictions.eq(propertyName, propertyValue);
		criteria.add(criterion);
		
		return (T) criteria.uniqueResult();
	}
	
	public Collection<T> getByForSet(String propertyName, Object[] propertyVals){
		Criteria criteria = getSession().createCriteria(entityClass);
		
		Criterion criterion = Restrictions.in(propertyName, propertyVals);
		criteria.add(criterion);
		
		return criteria.list();
	}
}

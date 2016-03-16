package com.atguigu.ems.test;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.atguigu.ems.entities.Employee;

public class HibernateTest {
	
	private ApplicationContext ctx = null;
	private SessionFactory sessionFactory = null;

	{
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		sessionFactory = ctx.getBean(SessionFactory.class);
	}
	
	@Test
	public void testTransactional(){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Employee employee = (Employee) session.get(Employee.class, 4);
		System.out.println(employee.getDepartment().getClass().getName());
		
		session.getTransaction().commit();
		
		//在事务提交后, 但 Session 关闭前, 若修改了 bean 的属性, 将不会再发送 update.
		//只要 Session 没关闭, 则其关联的属性依然可以获取. 
		employee.setLoginName(employee.getLoginName() + "AAAA");
		System.out.println(employee.getDepartment().getDepartmentName());
		
		session.close();
	}
	
	@Test
	public void test() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Employee employee = (Employee) session.get(Employee.class, 95);
		employee.setLoginName("AABBCC");
		session.evict(employee);
		
		Criteria criteria = session.createCriteria(Employee.class);
		Criterion criterion = Restrictions.eq("loginName", "AABBCC");
		criteria.add(criterion);
		
		List<Employee> emps = criteria.list();
		for(Employee emp: emps){
			System.out.println("-->" + emp.getEmployeeId());
		}
		
//		session.getTransaction().commit();
		session.close();
	}

}

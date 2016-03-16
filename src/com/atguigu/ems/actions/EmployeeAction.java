package com.atguigu.ems.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.WebUtils;

import com.atguigu.ems.entities.Employee;
import com.atguigu.ems.exceptions.LoginNameExistException;
import com.atguigu.ems.exceptions.LoginNameNotFoundException;
import com.atguigu.ems.exceptions.LoginNameNotMatchPasswordException;
import com.atguigu.ems.orm.EmployeeCrieriaFormBean;
import com.atguigu.ems.orm.Page;
import com.atguigu.ems.services.DepartmentService;
import com.atguigu.ems.services.EmployeeService;
import com.atguigu.ems.services.RoleService;
import com.opensymphony.xwork2.ActionContext;

@Scope("prototype")
@Controller
public class EmployeeAction extends BaseAction<Employee>{

	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private RoleService roleService;
	
	public EmployeeService getEmployeeService(){
		return (EmployeeService) service;
	}

	//文件上传相关
	private File file;
	
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * 如何实现和具体实体类无关的带查询条件的分页 ?
	 * 1. 页面的查询条件如何传过来 ? 或者说 传过来是什么样子 ?
	 * 通过表单域传递查询条件. 即通过 input 传递。 
	 * <input type="text" name="" value=""/>
	 * 一般地, 
	 * name 可以指定和哪个字段相比较.
	 * value 可以指定比较的值是多少.
	 * 
	 * value 不能做任何的修改, 修改 name 属性值. 
	 * 比较的方式比较的类型_比较的属性的名字.
	 * LIKES_loginName
	 * EQI_age
	 * GTL_salary
	 * 
	 * 这也意味着需要定义一个类来包装查询条件.
	 * 
	 * class PropertyFilter{
	 * 	
	 * 	private String propertyName;
	 * 	private Object propertyVal;
	 * 
	 * 	private MatchType matchType;
	 * 	private PropertyType propertyType;
	 * 
	 * 	enum MatchType{
	 * 		LIKE, EQ, GT, GE, LT, LE, ISNULL;
	 * 	}
	 * 
	 * 	enum PropertyType{
	 * 		I(Ingeger.class), L(Long.class), S(String.class), F(Float.class), D(Date.class);
	 * 
	 * 		private Class propertyType;
	 * 
	 * 		PropertyType(Class propretyType){
	 * 			this.propertyType = propertyType;
	 * 		}
	 * 
	 * 		Class getPropertyType(){
	 * 			return this.propretyType;
	 * 		}
	 * 	}
	 * }
	 * 
	 * 2. 在 Action 的方法中, 如何来获取查询条件对应的字段 ?
	 * 不能一个一个的调用 getParameter 方法.
	 * 于是把所有查询条件的字段的 name 属性值前添加一个前缀: filter_. 即
	 * <input type="text" name="filter_LIKES_loginName" value=""/> 	
	 * 在 Action 中获取指定前缀的请求参数即可. 
	 * 
	 * 3. 如何把页面传过来的参数转为一个一个的查询条件 ?
	 * 页面上的一个 input 标签应该能够对应一个查询条件!
	 * 一个查询条件必须的要素有什么 ?
	 * 和哪个字段比较. 即实体类的属性名. 
	 * 怎么比. 例如 > 还是 < 还是 = 还是 LIKE 获取其他
	 * 比较的值是什么. 
	 * 比较的值的类型是什么. 因为页面上传入的是字符串, 所以可能需要把字符串转为指定的类型. 
	 * 
	 * 4. Dao 方法如何进行分页 ?
	 * 步骤:
	 * 1). 在 Action 中调用 WebUtils#getParameterStartingWith() 方法来获取指定前缀的请求参数对应的 Map
	 * 2). 把 Map 转为 PropertyFilter 的集合. 
	 * 3). 把 PropertyFilter 的集合转为 Criterion 的集合.
	 * 4). 在 Dao 中使用 QBC 完成分页. 
	 * 
	 * 5. 分页实现后, 如何在点击 "上一页" 等连接时能够保持查询条件 ?
	 * 在 Action 中把查询条件对应的 Map 转为一个查询字符串, 例如: filter_LIKES_LoginName=a&filter_GTI_age=23
	 * 把该字符串传回到页面上.
	 * 在页面的翻页的超链接后面附着该查询字符串. 
	 * 
	 */
	public String list3(){
		HttpServletRequest req = ServletActionContext.getRequest();
		//得到的 Map 的键已经 remove 了前缀. 
		//{EQI_department.departmentId=1, EQI_enabled=1, EQS_gender=1, LIKES_email=c, LIKES_employeeName=b, LIKES_loginName=a}
		Map<String, Object> params = WebUtils.getParametersStartingWith(req, "filter_");
		
		page = service.getPage(page, params);
		request.put("page", page);
		
		//把 params 对应的 Map 再转为一个查询字符串. 
		//filter_EQI_department.departmentId=1&filter_EQI_enabled=1&filter_EQS_gender=1
		String queryString = parseParamMapToQueryString(params);
		request.put("queryString", queryString);
		
		return "list";
	}
	
	public String parseParamMapToQueryString(Map<String, Object> params){
		if(params != null && params.size() > 0){
			StringBuilder queryString = new StringBuilder();
			
			for(Map.Entry<String, Object> entry: params.entrySet()){
				String key = entry.getKey();
				Object val = entry.getValue();
				
				queryString.append("filter_")
				           .append(key)
				           .append("=")
				           .append(val)
				           .append("&");
			}
			queryString.replace(queryString.length() - 1, queryString.length(), "");
			return queryString.toString();
		}
		
		return "";
	}
	
	public void prepareCriteriaInput(){
		//创建一个 EmployeeCrieriaFormBean 对象.
		//并把其放到值栈的栈顶. 
		//需要手工放. 而不能使用 ModelDriven. 因为 ModelDriven 是放 Model 的
		ActionContext.getContext().getValueStack().push(new EmployeeCrieriaFormBean());
	}
	
	//显示查询条件的页面
	public String criteriaInput(){
		this.request.put("departments", departmentService.getAll());
		return "criteria-input";
	}
	
	public String uploadTemplateDownload() throws IOException{
		contentType = "application/vnd.ms-excel";
		
		String excelFileName = ServletActionContext.getServletContext().getRealPath("/files/employees.xls");
		inputStream = new FileInputStream(excelFileName);
		contentLength = inputStream.available();
		fileName = "employees.xls";
		
		return "excel-result";
	}
	
	public String upload() throws InvalidFormatException, IOException{
		//解析上传的 Excel 文件, 并把其中的数据传到服务端.
		List<String[]> errors = getEmployeeService().upload(file);
		
		if(errors != null && errors.size() > 0){
			StringBuilder errorMessages = new StringBuilder();
			
			for(String[] error: errors){
				String errorMessage = getText("error.employee.upload", error);
				errorMessages.append(errorMessage);
			}
			
			addActionError(errorMessages.toString());
			return "input";
		}
		
		return "upload-success";
	}
	
	//下载 Excel 文档. 
	public String downToExcel() throws IOException{
		//初始化文件下载的相关属性.
		contentType = "application/vnd.ms-excel";
		
		String excelFileName = ServletActionContext.getServletContext().getRealPath("/files/" + System.currentTimeMillis() + ".xls");
		System.out.println(excelFileName);
		request.put("excelFileName", excelFileName);
		
		getEmployeeService().downLoad(excelFileName);
		inputStream = new FileInputStream(excelFileName);
		
		contentLength = inputStream.available();
		fileName = "employees.xls";
		
		return "excel-result";
	}
	
	public void prepareSave(){
		if(id == null)
			model = new Employee();
		else{
			model = getEmployeeService().get(id);
			model.getRoles().clear();
			model.setDepartment(null);
		}
	}
	
	public String save(){
		String oldLoginName = "";
		
		try {
			String [] vals = params.get("oldLoginName");
			if(vals != null && vals.length == 1){
				oldLoginName = vals[0];
			}
			
			getEmployeeService().save(model, oldLoginName);
		} catch (LoginNameExistException e) {
			addFieldError("loginName", getText("error.employee.save.loginName", Arrays.asList(model.getLoginName()))); 
			return "emp-save-input";
		}
		
		return "emp-success";
	}
	
	public void validateLoginName() throws IOException{
		String result = "2";
		
		String loginName = params.get("loginName")[0];
		try {
			getEmployeeService().login(loginName, "");
		} catch (LoginNameNotFoundException e) {
			result = "1";
		} catch (LoginNameNotMatchPasswordException e) {
			result = "0";
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.getWriter().print(result);
	}
	
	/**
	 * 在修改出错的情况下:
	 * 1. 使用 chain 结果类型转发(即一个请求)到 emp-input 的这个 action
	 * 2. 先执行 prepareInput 方法: 从 Session 中获取 id 对应的对象 model
	 * 3. 然后执行 params 拦截器, 更新 model 的部分属性.
	 * 4. 进行页面的回显.
	 * 5. 整个请求结束, 是否会发出 update 语句来更新  model 对应的记录呢 ? 
	 * 
	 * 事实上: OpenSessionInView 做的事情是把 Session 开启的时间延长, 但事务还是在 Service 方法结束后提交的. 
	 * 所以后面不会有更新的语句. 
	 */
	public void prepareInput(){
		if(id != null)
			model = service.get(id);
	}
	
	public String input(){
		this.request.put("departments", departmentService.getAll());
		this.request.put("roles", roleService.getAll());
		
		return "emp-input";
	}
	
	//如何使用 Struts2 返回一个 JSON 数据呢 ?
	//1. 加入 struts2-json-plugin-2.3.15.3.jar 
	//2. strus2 配置文件中继承的包由 struts-default 修改为 json-default
	//3. 使用 json 结果类型.
	//4. 可以通过设置 root 属性, 使 Struts2 只返回 Action root 属性对应的 JSON 数据.
	//5. 使用 jons result 的 excludeProperties 属性设置不需要包含的属性. 
	//注意: 使用类似于 content.*\.roles 的语法来剔除集合中原生不包含的属性. 
	public String list2(){
		this.page = service.getPage(page);
		return "list2";
	}
	
	//ajax 直接返回标记位. 所以该方法没有返回值, 直接把结果 print 回去.
	public void delete() throws IOException{
		//直接返回标记位. 1, 2 或 其他值
		String result = "0";
		result = getEmployeeService().delete(id) + "";
		HttpServletResponse response = ServletActionContext.getResponse();
		response.getWriter().print(result);
	}
	
	public String list(){
		this.page = service.getPage(page);
		return "list";
	}
	
	public void prepareLogin(){
		model = new Employee();
	}
	
	public String login(){
		String loginName = model.getLoginName();
		String password = model.getPassword();
		
		Employee employee = getEmployeeService().login(loginName, password);
		session.put("employee", employee);
		
		return SUCCESS;
	}
}

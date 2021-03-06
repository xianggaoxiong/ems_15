package com.atguigu.ems.orm;

import java.util.List;

public class Page<T> {

	private int pageNo = 1;
	private int pageSize = 5;

	private long totalElements;
	private List<T> content;

	//4. 有没有前一页
	public boolean isHasPrev(){
		return (this.pageNo > 1);
	}
	
	//5. 有没有后一页
	public boolean isHasNext(){
		return (this.pageNo < getTotalPages());
	}
	
	//3. 获取总的页数的方法: 可以通过计算得到
	public int getTotalPages(){
		int totalPages = (int)totalElements / pageSize;
		
		if(totalElements % pageSize != 0){
			totalPages++;
		}
		
		return totalPages;
	}
	
	public int getPageNo() {
		return pageNo;
	}

	//1. 验证 pageNo 的合法性: 只能验证 pageNo 是否大于 0 
	public void setPageNo(int pageNo) {
		if(pageNo < 1){
			pageNo = 1;
		}
		
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalElements() {
		return totalElements;
	}

	//2. 先查询总的记录数. 并获取总页数, 并验证 pageNo 的合法性. 
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
		
		if(this.pageNo > getTotalPages()){
			this.pageNo = getTotalPages();
		}
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

}

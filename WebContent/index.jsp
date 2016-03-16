<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/common.jsp" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>欢迎使用培训中心管理系统</title>

<link rel="stylesheet" type="text/css" href="css/login.css">
<script type="text/javascript" src="${ctp }script/jquery.min.js"></script>
<script type="text/javascript">

   /*
	* 前端基于 JS 的验证.
	* 
	* ①: loginName 和 password 字段除去前后空格不能为空
	* ②: loginName 和 password 字段除去前后空格, 不能少于 6 个字符
	* ③: loginName 中不能包含特殊字符, 即以字母开头, 后边还可以包含数字和_ 
	*
	* 其中前两个验证都通过, 再验证 ③. 
	*/
	/*
	$(function(){
		$(".submit").click(function(){
			var flag = true;
			
			$("input:not('.submit')").each(function(){
				var val = this.value;
				val = $.trim(val);
				//把文本框中的空格去除.
				this.value = val;
				
				//获取 label 值
				var label = $(this).prev("b").text();
				
				//①: loginName 和 password 字段除去前后空格不能为空
				if(val == ""){
					alert(label + "不能为空!");
					flag = false;
				}
				//②: loginName 和 password 字段除去前后空格, 不能少于 6 个字符
				else if(val.length < 6){
					alert(label + "不能少于6个字符");
					flag = false;
				}
			});
			
			//③: 在前两个验证通过的情况下。 验证 loginName 中不能包含特殊字符, 即以字母开头, 后边还可以包含数字和_ 
			if(flag){
				//编写 js 的正则表达式.
				var reg = /^[a-zA-Z]\w+\w$/g;
				var loginName = $("input[name='loginName']").val();
				loginName = $.trim(loginName);
				
				if(!reg.test(loginName)){
					alert("登录名不合法!");
					flag = false;
				}
			}
			
			return flag;
		});
	})
	*/
	
</script>

</head>
<body>

	<div align="center">
		<s:form action="/emp-login" method="post">
			<div class="login_div" align="center">
				<font color="red">
					<!-- 显示复杂验证的错误消息 -->
					<s:text name="%{exception.class.name}"></s:text>
				</font>
				
				<b>用户名</b>
				<s:textfield name="loginName"></s:textfield>
				<font color="red">
					<!-- 显示简单验证的错误消息 -->
					<s:fielderror fieldName="loginName"></s:fielderror>
				</font>

				<b>密码</b>
				<s:password name="password"></s:password>
				<font color="red">
					<s:fielderror fieldName="password"></s:fielderror>
				</font>
				<input type="submit" class="submit" value="登录" />
			</div>		
		</s:form>
	</div>	

</body>
</html>
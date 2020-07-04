<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	String admin_id = (String)session.getAttribute("adminOk");
	
	if(admin_id == null) {
%>
	<script>
		alert("관리자 로그아웃 되었습니다.");
		location.href = "../guest/guest_index.jsp";
	</script>
<%
		return;
	}
%>
<table style="width: 80%">
	<tr style="background-color: #ccbbcc; text-align: center;" >
		<td><a href="../index.jsp">홈페이지</a></td>
		<td><a href="admin_login_out.jsp">로그아웃</a></td>
		<td><a href="membermanager.jsp">회원관리</a></td>
		<td><a href="productmanager.jsp">상품관리</a></td>
		<td><a href="ordermanager.jsp">주문관리</a></td>
	</tr>
</table>
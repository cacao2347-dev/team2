<%@page import="com.test.BoardDTO"%>
<%@page import="java.util.List"%>
<%@page import="com.test.MyUtil"%>
<%@page import="com.test.BoardDAO"%>
<%@page import="com.util.DBConn"%>
<%@page import="java.sql.Connection"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>

<%

	Connection conn = DBConn.getConnection();
	BoardDAO dao = new BoardDAO(conn);
	MyUtil myUtil = new MyUtil();
	
	// 넘어온 페이지 번호 확인
	String pageNum = request.getParameter("pageNum");
	int currentPage = 1;	// 현재 표시되어야 하는 페이지

	if(pageNum != null)
		currentPage = Integer.parseInt(pageNum);
	
	// 전체 데이터 갯수 구하기
	int dataCount = dao.getDataCount();
	
	// 전체 데이터를 기준으로 총 페이지 수 계산
	int numPerPage = 10;	//-- 한 페이지에 표시할 데이터 갯수
	int totalPage = myUtil.getPageCount(numPerPage, dataCount);
	
	// 전체 페이지 수 보다 표시할 페이지가 큰 경우
	// 표시할 페이지를 전체 페이지로 구성
	//-- 한 마디로, 데이터를 삭제해서 페이지가 줄었을 경우
	if (currentPage > totalPage)
		currentPage = totalPage;
	
	// 데이터베이스로부터 가져올 시작과 끝 위치
	int start = (currentPage-1) * numPerPage + 1;	/* 현재 페이지 1 (1-1)*10 + 1 = 1; */
	int end = currentPage * numPerPage;				/* 1*10 = 10까지 1부터 10까지 표시 */
	
	List<BoardDTO> lists = dao.getLists(start, end);
	
	// 페이징 처리
	
	String param = "";
	
	String listUrl = "List.jsp" + param;
	String pageIndexList = myUtil.getIndexList(currentPage, totalPage, listUrl);
	
	// 글 내용 보기 주소
	String articleUrl = cp + "/Article.jsp";
	
	if (param.equals(""))
		articleUrl = articleUrl + "?pageNum=" + currentPage;
	else
		articleUrl = articleUrl + param + "&pageNum=" + currentPage;
	
	
	DBConn.close();
	
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>List.jsp</title>
<link rel="stylesheet" type="text/css" href="<%=cp %>/css/style.css">
<link rel="stylesheet" type="text/css" href="<%=cp %>/css/list.css">
<script type="text/javascript">

	function sendIt()
	{
		alert("검색 버튼 클릭");
	}
	
</script>
	
</head>

<body>

<div id="bbsList">

	<div id="bbsList_title">
		게 시 판 (JDBC 연동 버전)
	</div><!-- #bbsList -->
	
	<div id="bbsList_header">
		<div id="leftHeader">
			<form action="" name="searchForm" method="post">
				<select name="searchKey" class="selectFiled">
					<option value="subject">제목</option>
					<option value="name">작성자</option>
					<option value="content">내용</option>
				</select>
				<input type="text" name="searchValue" class="textFiled">
				<input type="button" value="검색" class="btn2"
				onclick="sendIt()">
			</form>
		</div><!-- #leftHeader -->
		
		<div id="rightHeader">
			<input type="button" value="글올리기" class="btn2"
			onclick="javascript:location.href='<%=cp %>/Create.jsp'">
		</div><!-- #rightHeader -->
		
	</div><!-- #bbsList_header -->
	
	<div id="bbsList_list">
		<div id="title">
			<dl>
				<dt class="num">번호</dt>
				<dt class="subject">제목</dt>
				<dt class="name">작성자</dt>
				<dt class="created">작성일</dt>
				<dt class="hitCount">조회수</dt>
			</dl>
		</div><!-- #title -->
		
		<div id="lists">
			<!-- 
			<dl>
				<dd class="num">1</dd>
				<dd class="subject">안녕하세요</dd>
				<dd class="name">김태균</dd>
				<dd class="created">2020-05-15</dd>
				<dd class="hitCount">0</dd>
			</dl> 
			-->
			<%
			for(BoardDTO dto : lists)
			{
			%>
			<dl>
				<dd class="num"><%=dto.getNum() %></dd>
				
				<dd class="subject">
				<a href="<%=articleUrl%>&num=<%=dto.getNum() %>">
				<%=dto.getSubject() %>
				</a>
				</dd>
				
				<dd class="name"><%=dto.getName() %></dd>
				<dd class="created"><%=dto.getCreated() %></dd>
				<dd class="hitCount"><%=dto.getHitCount() %></dd>
			</dl>
			<%
			}
			%>
		</div><!-- #lists -->
		
		<div id="footer">
			<!-- <p> 1 Prev 21 22 23 24 25 26 27 28 29 30 Next 90 </p> -->
			<!-- <p> 등록된 게시물이 없습니다. </p> -->
			<p>
			<%
			if (dataCount != 0)
			{
			%>
				<%=pageIndexList %>
			<%
			}
			else
			{
			%>
				등록된 게시물이 없습니다.
			<%
			}
			%>
			</p>
		</div><!-- #footer -->
		
	</div><!-- #bbsList_list -->	
	
	
</div><!-- #bbsList -->

</body>
</html>
/*=================
   BoardDAO.java
=================*/

package com.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO
{
	private Connection conn;

	public BoardDAO(Connection conn)
	{
		this.conn = conn;
	}

	// 게시물 번호의 최대값 얻어내기
	public int getMaxNum() throws SQLException
	{
		int result = 0;

		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";

		try
		{
			sql = "SELECT NVL(MAX(NUM), 0)  AS MAXNUM FROM TBL_BOARD";

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);

			if (rs.next())
				result = rs.getInt(1);

			rs.close();
			stmt.close();

		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;

	}// end getMaxNum()

	// 게시물 작성 → 데이터 입력
	public int insertData(BoardDTO dto)
	{
		// hitCount 는 기본값 0 또는 default 또는 입력항목 생략
		// created 는 기본값 sysdate 또는 default 또는 입력항목 생략

		int result = 0;
		PreparedStatement pstmt = null;
		String sql = "";

		try
		{
			sql = "INSERT INTO TBL_BOARD(NUM, NAME, PWD, EMAIL, SUBJECT," + " CONTENT, IPADDR, HITCOUNT, CREATED)"
					+ " VALUES(?, ?, ?, ?, ?, ?, ?, 0, SYSDATE)";

			/*
			 * sql = "INSERT INTO TBL_BOARD(NUM, NAME, PWD, EMAIL, SUBJECT," +
			 * " CONTENT, IPADDR, HITCOUNT, CREATED)" +
			 * " VALUES(?, ?, ?, ?, ?, ?, ?, default , default)";
			 */

			/*
			 * sql = "INSERT INTO TBL_BOARD(NUM, NAME, PWD, EMAIL, SUBJECT," +
			 * " CONTENT, IPADDR)" + " VALUES(?, ?, ?, ?, ?, ?, ?)";
			 */

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, dto.getPwd());
			pstmt.setString(4, dto.getEmail());
			pstmt.setString(5, dto.getSubject());
			pstmt.setString(6, dto.getContent());
			pstmt.setString(7, dto.getIpAddr());

			result = pstmt.executeUpdate();

			pstmt.close();

		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;

	}// end insertData()

	// DB 레코드의 갯수를 가져오는 메소드 정의
	// → 검색 기능을 추가하게 되면 수정하게 될 메소드
	public int getDataCount()
	{
		int result = 0;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";

		try
		{
			sql = "SELECT COUNT(*) AS COUNT FROM TBL_BOARD";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next())
				result = rs.getInt(1);

			rs.close();
			stmt.close();

		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;

	}// end getDataCount()
		// getDataCount().png 참조

	// 특정 영역의(시작번호 ~ 끝번호) 게시물의 목록을 읽어오는 메소드 정의
	public List<BoardDTO> getLists(int start, int end)
	{
		List<BoardDTO> result = new ArrayList<BoardDTO>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try
		{
			sql = "SELECT NUM, NAME, SUBJECT, HITCOUNT, CREATED"
					+ " FROM(SELECT ROWNUM AS RNUM, DATA.* FROM"
					+ "(SELECT NUM, NAME, SUBJECT, HITCOUNT, TO_CHAR(CREATED, 'YYYY-MM-DD') AS CREATED"
					+ " FROM TBL_BOARD ORDER BY NUM DESC) DATA) WHERE RNUM >= ? AND RNUM <= ?";
			
			pstmt = conn.prepareStatement(sql);
			

			pstmt.setInt(1, start);
			pstmt.setInt(2, end);

			rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				BoardDTO dto = new BoardDTO();
				dto.setNum(rs.getInt("NUM"));
				dto.setName(rs.getString("NAME"));
				dto.setSubject(rs.getString("SUBJECT"));
				dto.setHitCount(rs.getInt("HITCOUNT"));
				dto.setCreated(rs.getString("CREATED"));

				result.add(dto);
			}

			rs.close();
			pstmt.close();

		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;
	}

	// 특정 게시물 조회에 따른 조회 횟수 증가 메소드 정의
	public int updateHitCount(int num)
	{
		int result = 0;
		String sql = "";
		PreparedStatement pstmt = null;

		try
		{
			sql = "UPDATE TBL_BOARD SET HITCOUNT = HITCOUNT + 1 WHERE NUM=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();

			pstmt.close();

		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;
	}// end updateHitCount()

	// 특정 게시물의 내용을 읽어오는 메소드 정의(한개의 게시물만 읽어와서)
	// (num은 프라이머리 제약조건 설정되어 있어서 하나만 읽어옴)
	public BoardDTO getReadData(int num)
	{
		BoardDTO result = null;
		PreparedStatement pstmt;
		ResultSet rs = null;
		String sql = "";

		try
		{
			sql = "SELECT NUM, NAME, SUBJECT, PWD, EMAIL," + " SUBJECT, CONTENT, IPADDR, HITCOUNT, CREATED"
					+ " FROM TBL_BOARD WHERE NUM=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				result = new BoardDTO();
				result.setNum(rs.getInt("NUM"));
				result.setName(rs.getString("NAME"));
				result.setPwd(rs.getString("PWD"));
				result.setEmail(rs.getString("EMAIL"));
				result.setSubject(rs.getString("SUBJECT"));
				result.setContent(rs.getString("CONTENT"));
				result.setIpAddr(rs.getString("IPADDR"));
				result.setHitCount(rs.getInt("HITCOUNT"));
				result.setCreated(rs.getString("CREATED"));
			}
			rs.close();
			pstmt.close();

		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;

	}// end getReadData()

	// 특정 게시물을 삭제하는 기능의 메소드 정의
	public int deleteData(int num)
	{
		int result = 0;
		String sql = "";
		PreparedStatement pstmt = null;

		try
		{
			sql = "DELETE FROM TBL_BOARD WHERE NUM=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();

			pstmt.close();

		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;

	}// end deleteData()

	public int updateData(BoardDTO dto)
	{
		int result = 0;

		String sql = "";
		PreparedStatement pstmt = null;

		try
		{
			sql = "UPDATE TBL_BOARD" + " SET NAME=?, PWD=?, SUBJECT=?" + " ,EMAIL=?,CONTENT=?" + " WHERE NUM=?";
			pstmt = conn.prepareStatement(sql);
			// dto를 매개변수로 사용하므로 가져올수 있다. 가져온 값이 입력한 값이 된다.
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getPwd());
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(1, dto.getEmail());
			pstmt.setString(1, dto.getContent());
			pstmt.setInt(1, dto.getNum());

			result = pstmt.executeUpdate();

			pstmt.close();

		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;
	}

	// 다음 게시물 번호를 얻어오는 메소드 정의
	public int getNextNum(int num)
	{
		int result = 0;

		String sql = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			sql = "SELECT NVL(MIN(NUM), -1) AS NEXTNUM FROM TBL_BOARD WHERE NUM>?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			if(rs.next())
				result = rs.getInt("NEXTNUM");
			
		} 
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
		return result;
		
	}

	// 이전 게시물 번호를 얻어오는 메소드 정의
	public int getBeforeNum(int num)
	{
		int result = 0;

		String sql = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			sql = "SELECT NVL(MAX(NUM),-1) AS BEFORENUM FROM TBL_BOARD WHERE NUM<?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			if(rs.next())
				result = rs.getInt("BEFORENUM");
			
		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		return result;
	}

}

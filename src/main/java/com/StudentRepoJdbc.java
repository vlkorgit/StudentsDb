package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.security.Key;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class StudentRepoJdbc implements StudentRepo {
	private JdbcTemplate jdbc;

	private int studentsAmount;
	private boolean amountCached = false;

	@Autowired
	public StudentRepoJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public List<Student> getAllStudents() {
		return jdbc.query("select * from Students", this::rowMapper);
	}

	private Student rowMapper(ResultSet resultSet, int i) throws SQLException {
		var student = new Student();
		student.setId(resultSet.getInt("Id"));
		student.setFirstName(resultSet.getString("FirstName"));
		student.setLastName(resultSet.getString("LastName"));
		student.setMiddleName(resultSet.getString("MiddleName"));
		student.setDob(resultSet.getDate("DateOfBirth"));
		student.setGroup(resultSet.getInt("GroupNumber"));
		return student;
	}

	private Student rowMapperWithTotal(ResultSet resultSet, int i) throws SQLException {
		studentsAmount = resultSet.getInt("total");
		amountCached = true;
		return rowMapper(resultSet, i);
	}

	@Override
	public List<Student> getStudentsPage(int page, int pagesize) {
		if (page <= 0 || pagesize <= 0) throw new RuntimeException("Parameters must be positive");
		var request = pageSql.replaceAll("@p", String.valueOf(page)).replaceAll("@n", String.valueOf(pagesize));
		return jdbc.query(request, this::rowMapper);
	}

	@Override
	public int getStudentsAmount() {
		if (amountCached) return studentsAmount;
		else return jdbc.queryForObject("select count(*) as amount from Students", (rs, i) -> rs.getInt("amount"));
	}

	@Override
	public Student getStudentById(int id) {
		return jdbc.queryForObject("select * from Students where Students.id = ?", this::rowMapper, id);
	}

	@Override
	public Student addStudent(Student student) {
		amountCached = false;
		KeyHolder kh = new GeneratedKeyHolder();
		var prepStatement = new PreparedStatementCreatorFactory("insert into Students (FirstName,LastName,MiddleName," +
				"DateOfBirth,GroupNumber) " +
				"values (?,?,?,?,?)", Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.INTEGER)
				.newPreparedStatementCreator(Arrays.asList(student.getFirstName(), student.getLastName(),
						student.getMiddleName(), student.getDob(), student.getGroup()));
		jdbc.update(prepStatement, kh);
		//student.setId(kh.getKey().intValue());
		return student;
	}

	@Override
	public void removeStudent(Student student) {
		amountCached = false;
		jdbc.update("delete from Students where Students.Id = ?", student.getId());
	}

	@Override
	public void removeStudentById(int id) {
		amountCached = false;
		jdbc.update("delete from Students where Students.Id = ?", id);
	}

	private static String pageSql = "SELECT * FROM  " +
			"(SELECT *, " +
			"CASE WHEN num % @n = 0 THEN num/@n ELSE num/@n + 1 END AS page_num,  " +
			"CASE WHEN total % @n = 0 THEN total/@n ELSE total/@n + 1 END AS num_of_pages  " +
			"FROM  " +
			"(SELECT *,	 " +
			"ROW_NUMBER() OVER(ORDER BY Id) AS num,  " +
			"COUNT(*) OVER() AS total  " +
			"FROM Students  " +
			") X  " +
			") Y  " +
			"WHERE page_num = @p  ";
}

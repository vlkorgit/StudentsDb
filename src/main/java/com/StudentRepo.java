package com;

import java.util.List;

public interface StudentRepo {
	public List<Student> getAllStudents();
	public List<Student> getStudentsPage(int page,int pagesize);
	public int getStudentsAmount();
	public Student getStudentById(int id);
	public Student addStudent(Student student);
	public void removeStudent(Student student);
	public void removeStudentById(int id);
}

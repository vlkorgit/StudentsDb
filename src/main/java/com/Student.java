package com;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
public class Student {
	private int Id;
	private String FirstName;
	private String LastName;
	private String MiddleName;
	private Date Dob;
	private int Group;
}

package com;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.naming.factory.ResourceLinkFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.validation.Errors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class StudentValidator {
	@Getter
	@Setter
	private String FirstName;
	@Getter
	@Setter
	private String LastName;
	@Getter
	@Setter
	private String MiddleName;
	@Getter
	@Setter
	private String Dob;
	@Getter
	@Setter
	private String Group;
	private Boolean hasErrors = false;
	private Boolean validated = false;

	public Boolean hasErrors() {
		return hasErrors;
	}

	public Boolean isValidated() {
		return validated;
	}

	public Set<ValidationResult> validate() {
		var result = new HashSet<ValidationResult>();
		result.add(validateFirstName());
		result.add(validateLastName());
		result.add(validateDob());
		result.add(validateGroup());
		validated = true;
		return result;
	}

	private ValidationResult validateFirstName() {
		String msg = "Ok";
		Boolean error = true;
		if (FirstName == null)
			msg = "Name must be not null";
		else if (FirstName.isEmpty())
			msg = "Name must be not empty";
		else if (FirstName.isBlank())
			msg = "Name must be bot blank";
		else if (FirstName.length() > 50 || FirstName.length() < 1)
			msg = "Name must be over than 1 and less than 50 characters";
		else
			error = false;
		if (error) hasErrors = true;
		return new ValidationResult("FirstName", msg, error);

	}

	private ValidationResult validateLastName() {
		String msg = "Ok";
		Boolean error = true;
		if (LastName == null)
			msg = "Last name must be not null";
		else if (LastName.isEmpty())
			msg = "Last name must be not empty";
		else if (LastName.isBlank())
			msg = "Last name must be bot blank";
		else if (LastName.length() > 50 || LastName.length() < 1)
			msg = "Last name must be over than 1 and less than 50 characters";
		else
			error = false;
		if (error) hasErrors = true;
		return new ValidationResult("LastName", msg, error);
	}

	private ValidationResult validateDob() {
		String msg = "Ok";
		Boolean error = true;
		try {
			var date = new SimpleDateFormat("yyyy-mm-dd").parse(Dob);
			if (date.after(new Date())) msg = "Date must be before today";
			else error = false;
		} catch (ParseException e) {
			msg = "Wrong date format";
		}
		if (error) hasErrors = true;
		return new ValidationResult("Dob", msg, error);
	}

	private ValidationResult validateGroup() {
		String msg = "Ok";
		Boolean error = true;
		try {
			var group = Integer.valueOf(Group);
			if (group > 1000 || group < 1) msg = "Group number must be from 1 to 1000";
			else error = false;
		} catch (NumberFormatException e) {
			msg = "Group number must be from 1 to 1000";
		}
		if (error) hasErrors = true;
		return new ValidationResult("Group", msg, error);
	}

	@Data
	class ValidationResult {
		private final String FieldName;
		private final String Message;
		private final Boolean Error;

	}

	public Student parse() {
		if (!validated) throw new RuntimeException("StudentValidator wasn't validated before parsing");
		if (hasErrors) throw new RuntimeException("StudentValidator has errors");
		var student = new Student();
		student.setFirstName(FirstName);
		student.setLastName(LastName);
		student.setMiddleName(MiddleName);
		try {
			student.setGroup(Integer.valueOf(Group));
			student.setDob((new SimpleDateFormat("yyyy-mm-dd").parse(Dob)));
		} catch (Exception e) {
			throw new RuntimeException("Unreachable code");
		}
		return student;
	}
}

package com.example.employee.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Employee {

    @NotEmpty(message = "ID cant be empty")
    @Size(min =3, message = "Length of ID has to be more than 2.")
    private String ID;
    @NotEmpty(message = "Username can't be empty. Please enter your name")
    @Size(min = 5, message = "Length must be more than 4 characters in Name")
    private String name;
    @Email(message = "Wrong email format.")
    private String email;
    @Pattern(regexp = "^05[0-9]*$", message = "Number Must Start with 05")
    @Size(min =10, max=10, message = "Number must consists of 10 digits only")
    private String phoneNumber;
    @NotNull(message = "must enter age")
    @Min(value = 26, message = "Age must be older than 25.")
    private int age;
    @NotEmpty(message ="must enter position")
    private String position;
    private boolean onLeave = false;
    @NotNull(message = "Enter the hiring date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Hiring date must be today on in the past.")
    private LocalDate hireDate;
    @NotNull(message = "Enter Annual leave")
    @Min(value = 1, message = "Annual leave to be a positive number. ")
    private int annualLeave;

}

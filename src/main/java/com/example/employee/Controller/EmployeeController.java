package com.example.employee.Controller;

import com.example.employee.Model.Employee;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    ArrayList<Employee> employees = new ArrayList<>();
    boolean flag;

    @PostMapping("/create")
    public ResponseEntity<?> createEmployee(@RequestBody @Valid Employee employee, Errors errors){
        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());
        }
        if (employee.isOnLeave())
            return ResponseEntity.status(400).body("We can't add an employee who is on leave.");

        if (employee.getPosition().equals("supervisor") || employee.getPosition().equals("coordinator")) {
            employees.add(employee);
            return ResponseEntity.status(200).body("Employee Added Successfully!");
        }
        else
            return ResponseEntity.status(400).body("Position has to be either coordinator OR supervisor.");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getEmployees(){
        return ResponseEntity.status(200).body(employees);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateEmployee(@RequestBody @Valid Employee employee, Errors errors){
        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());
        }
        flag= false;
        for ( Employee e : employees ){
            if ( e.getID().equals(employee.getID())) {
                flag = true;
                employees.remove(e); //.SET !
                employees.add(employee);
            }
        }
        if (flag)
            return ResponseEntity.status(200).body("Updated successfully");
        else
            return ResponseEntity.status(400).body("Employee with that ID does not exist");
    }
    @DeleteMapping("/delete/{ID}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String ID){
        for ( Employee e : employees ) {
            if (ID.equals(e.getID())) {
                employees.remove(e);
                return ResponseEntity.status(200).body("Employee Deleted");
            }
        }
        return ResponseEntity.status(400).body("Employee with that ID does not exist");
    }
    @GetMapping("/getbyposition/{position}")
    public ResponseEntity<?> getByPosition(@PathVariable String position){
        ArrayList<Employee> employeeByP = new ArrayList<>();
        if ( position.equals("supervisor")|| position.equals("coordinator")){
            for ( Employee e : employees )
                if ( e.getPosition().equals(position))
                    employeeByP.add(e);
            if (employeeByP.isEmpty())
                return ResponseEntity.status(200).body("There are no employees with that position in the system. please register a "+position);
            return ResponseEntity.status(200).body(employeeByP);
        }
        else
            return ResponseEntity.status(400).body("The position u choose has to be either \"coordinator\" OR a \"supervisor\"");
    }
    @GetMapping("/getbyage/{min}/{max}")
    public ResponseEntity<?> getByAge(@PathVariable int min, @PathVariable int max){
        if ( max >99 ){
            return ResponseEntity.status(400).body("Age range is invalid. max is 99.");
        }
        if ( min <25 ){
            return ResponseEntity.status(400).body("Age range is invalid. min is 26.");
        }
        ArrayList<Employee> empByAge = new ArrayList<>();
        for ( Employee e : employees ){
            if ( e.getAge() <= max && e.getAge() >= min )
                empByAge.add(e);
        }
        if ( empByAge.isEmpty())
            return ResponseEntity.status(200).body("Employees in that age range do not exist.");
        return ResponseEntity.status(200).body(empByAge);
    }

    @PutMapping("/applyforannualleave/{ID}")
    public ResponseEntity<?> applyAnnualLeave(@PathVariable String ID){
        flag = false;
        for ( Employee e : employees ){
            if ( ID.equals(e.getID()) ){
                if (e.isOnLeave())
                    return ResponseEntity.status(400).body("Employee is already on leave. can't apply");
                if ( e.getAnnualLeave() < 1 )
                    return ResponseEntity.status(400).body("Employee does not have leave days left. can't apply");
                e.setAnnualLeave(e.getAnnualLeave()-1);
                e.setOnLeave(true);
                flag = true;
            }
        }
        if (flag)
            return ResponseEntity.status(200).body("Your application for the leave have been accepted! wish you well");
        return ResponseEntity.status(400).body("Employee with the ID: "+ID+" does not exist");
    }

    @GetMapping ("/get-employees-with-no-annual-leaves")
    public ResponseEntity<?> getEmpWithNoAnnualLeave(){
        ArrayList<Employee> emp = new ArrayList<>();
        for ( Employee e : employees){
            if ( e.getAnnualLeave() < 1)
                emp.add(e);
        }
        if ( emp.isEmpty())
            return ResponseEntity.status(200).body("There are no employees with no Annual leave :)");
        return ResponseEntity.status(200).body(emp);
    }

    @PutMapping("/promote/{requesterID}/{employeeID}")
    public ResponseEntity<?> promoteEmployee(@PathVariable String requesterID, @PathVariable String employeeID){
        boolean flagRequester = false;
        for ( Employee e : employees ){
            if ( requesterID.equals(e.getID()) ) {
                flagRequester = true;
                if (!e.getPosition().equals("supervisor")) {
                    return ResponseEntity.status(400).body("You have no authority to promote this employee");
                }
            }
        }
        if ( !flagRequester )
            return ResponseEntity.status(400).body("Your ID is not registered in the system.");
        for ( Employee e : employees ) {
            if (employeeID.equals(e.getID())){
                if ( e.getAge() < 30 )
                    return ResponseEntity.status(400).body("Employee age is not valid for a promotion");
                if ( e.isOnLeave() )
                    return ResponseEntity.status(400).body("Employee currently is on leave and can't be promoted.");
                e.setPosition("supervisor");
                return ResponseEntity.status(200).body("Employee has been promoted successfully");
            }
        }
        return ResponseEntity.status(400).body("The employee that you're requesting to promote does not exist.");
    }
}

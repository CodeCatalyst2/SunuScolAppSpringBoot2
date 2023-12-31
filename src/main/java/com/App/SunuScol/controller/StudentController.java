package com.App.SunuScol.controller;

import com.App.SunuScol.model.ClassStudent;
import com.App.SunuScol.model.Student;
import com.App.SunuScol.model.User;
import com.App.SunuScol.service.ClassStudentService;
import com.App.SunuScol.service.StudentService;
import com.App.SunuScol.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class StudentController  {
    private  final StudentService studentService;
    private  final UserService userService;
    private  final ClassStudentService classStudentService;


    @Autowired
    public StudentController(StudentService studentService, UserService userService, ClassStudentService classStudentService){
        this.studentService = studentService;
        this.userService = userService;
        this.classStudentService = classStudentService;
    }

    //    Acquérir  les utilisateurs
    @RequestMapping(method = RequestMethod.GET, value = "/students")
    public List<Student> getStudents(){return studentService.getStudents();}

    //    Acquérir  un utilisateurs
    @RequestMapping(method = RequestMethod.GET, value = "/student/{id}")
    public Student getStudent(@PathVariable long id){return studentService.getStudent(id); }

    //    Ajouter un utilisateur, des étudiants et une classe
    @RequestMapping(method = RequestMethod.POST, value = "/user_students/{id}")
    public ResponseEntity<String> addUserStudents(@RequestBody User user) {
        User savedUser = userService.addUser(user);

        System.out.println(savedUser);
        if (savedUser != null) {
            // Vérifiez si l'utilisateur a des rôles dans son objet User
            if (user.getStudents() != null && !user.getStudents().isEmpty()) {
                for (Student student : user.getStudents()) {
                    student.setUserId(savedUser.getUserId());
                    // Enregistrez le rôle dans la base de données
//                    studentService.addStudent(student);
                }
//                return ResponseEntity.ok("Etudiant ajouté avec succès avec un rôle.");
            } else {
                return ResponseEntity.badRequest().body("L'étudiant n'a pas de rôle spécifié.");
            }
            // Check if the user has classstudents associated
            if (user.getClassstudents() != null && !user.getClassstudents().isEmpty()) {
                for (ClassStudent classStudent : user.getClassstudents()) {
                    classStudent.setUserId(savedUser.getUserId());
                    // Save the classStudent and ideally, this method should return the saved object
                    ClassStudent savedClassStudent = classStudentService.addClassStudent(classStudent);

                    // Assuming the savedClassStudent object has the generated classId.
                    Long generatedClassId = savedClassStudent.getClassId();

                    // Update the classId of each student associated with this classStudent
                    for (Student student : user.getStudents()) {
                         student.setClassId(generatedClassId);
                        // Now save this student with the updated classId
                        Student savedStudent = studentService.addStudent(student);

                        Long generatedStudentId = savedStudent.getStudentId();
                        Student fetchedStudent = studentService.getStudent(generatedStudentId);
                        savedClassStudent.getStudents().add(fetchedStudent);
                        classStudentService.updateClassStudent(savedClassStudent);
                    }
                }
            } else {
                return ResponseEntity.badRequest().body("No classstudents specified for the user.");
            }
            return ResponseEntity.ok("User, students, and classstudents added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Échec de l'ajout de l'utilisateur.");
        }
    }


    //    //    Ajouter un étudiant
    @RequestMapping(method =  RequestMethod.POST, value = "/student/{id}")
    public void addStudent(@RequestBody Student student){studentService.addStudent(student);}


    //Modifier les informations d'un utilisateur avec son élève (impossible de mettre à jour plus d'un élève)
    @RequestMapping(method = RequestMethod.PUT, value = "/user_students/{idUser}/{idStudent}")
    public ResponseEntity<String> updateUserStudents(@PathVariable("idUser") Long idUser, @PathVariable("idStudent") Long idStudent, @RequestBody User updatedUser) {
        // Vérifiez d'abord si l'utilisateur avec l'ID spécifié existe
        User existingUser = userService.getUser(idUser);

        if (existingUser != null) {
            // Mettez à jour les détails de l'utilisateur à partir de la requête
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setUserName(updatedUser.getUserName());

            // Enregistrez la mise à jour de l'utilisateur dans la base de données
            User savedUser = userService.updateUser(existingUser);

            // Vérifiez si l'utilisateur a des rôles dans son objet User
            if (updatedUser.getStudents() != null && !updatedUser.getStudents().isEmpty()) {
                for (Student student : updatedUser.getStudents()) {
                    // Assurez-vous que le rôle appartienne à l'utilisateur
                    student.setUserId(savedUser.getUserId());
                    student.setStudentId(idStudent);
                    // Enregistrez ou mettez à jour le rôle dans la base de données
                    studentService.updateStudent(student);
                }
            }
            return ResponseEntity.ok("Utilisateur mis à jour avec succès avec des rôles.");
        } else {
            return ResponseEntity.ok("Id de cet utilisateur n'existe pas .");
        }
    }

    //Modifier les informations d'un étudiant
    @RequestMapping(method =  RequestMethod.PUT, value = "/student/{id}")
    public void updateStudent(@RequestBody Student student, @PathVariable long id){
        student.setStudentId(id);
        studentService.updateStudent(student);
    }

    //Supprimer un Student
    @RequestMapping(method = RequestMethod.DELETE, value = "/student/{id}")
    public void deleteStudent(@PathVariable long id){studentService.deleteStudent(id);}

}


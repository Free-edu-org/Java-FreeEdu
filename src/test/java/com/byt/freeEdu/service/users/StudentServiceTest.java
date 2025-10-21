package com.byt.freeEdu.service.users;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.byt.freeEdu.mapper.RemarkMapper;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.DTO.StudentDto;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.users.Parent;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.RemarkRepository;
import com.byt.freeEdu.repository.StudentRepository;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest{

  @InjectMocks
  private StudentService studentService;

  @Mock
  private RemarkRepository remarkRepository;
  @Mock
  private RemarkMapper remarkMapper;
  @Mock
  private StudentRepository studentRepository;
  @Mock
  private UserService userService;

  // ===== addStudent =====

  @Test
  @DisplayName("addStudent: zapisuje i zwraca studenta")
  void addStudent_savesStudentSuccessfully() {
    Student student = new Student();
    student.setFirstname("Jan");
    when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

    Student result = studentService.addStudent(student);

    assertNotNull(result);
    assertEquals("Jan",result.getFirstname());
    verify(studentRepository,times(1)).save(student);
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== getAllStudentIds =====

  @Test
  @DisplayName("getAllStudentIds: zwraca listę identyfikatorów studentów")
  void getAllStudentIds_returnsIdsList() {
    Student s1 = new Student();
    s1.setUserId(1);
    Student s2 = new Student();
    s2.setUserId(2);
    when(studentRepository.findAll()).thenReturn(List.of(s1,s2));

    List<Integer> ids = studentService.getAllStudentIds();

    assertEquals(List.of(1,2),ids);
    verify(studentRepository).findAll();
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== getStudentById =====

  @Test
  @DisplayName("getStudentById: zwraca studenta gdy istnieje")
  void getStudentById_returnsStudent() {
    int id = 10;
    Student student = new Student();
    student.setUserId(id);
    when(studentRepository.findById(id)).thenReturn(Optional.of(student));

    Student result = studentService.getStudentById(id);

    assertNotNull(result);
    assertEquals(10,result.getUserId());
    verify(studentRepository).findById(id);
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  @Test
  @DisplayName("getStudentById: gdy brak, rzuca EntityNotFoundException")
  void getStudentById_studentNotFound_throwsException() {
    int id = 999;
    when(studentRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
        () -> studentService.getStudentById(id));

    assertTrue(ex.getMessage().contains(String.valueOf(id)));
    verify(studentRepository).findById(id);
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== getRemarksForStudent =====

  @Test
  @DisplayName("getRemarksForStudent: mapuje i zwraca listę RemarkDto")
  void getRemarksForStudent_returnsMappedList() {
    int studentId = 1;
    Remark remark1 = mock(Remark.class);
    Remark remark2 = mock(Remark.class);
    RemarkDto dto1 = new RemarkDto();
    RemarkDto dto2 = new RemarkDto();

    when(remarkRepository.findByStudentUserId(studentId)).thenReturn(List.of(remark1,remark2));
    when(remarkMapper.toDto(remark1)).thenReturn(dto1);
    when(remarkMapper.toDto(remark2)).thenReturn(dto2);

    List<RemarkDto> result = studentService.getRemarksForStudent(studentId);

    assertEquals(2,result.size());
    verify(remarkRepository).findByStudentUserId(studentId);
    verify(remarkMapper).toDto(remark1);
    verify(remarkMapper).toDto(remark2);
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== getAllStudents =====

  @Test
  @DisplayName("getAllStudents: zwraca listę studentów")
  void getAllStudents_returnsList() {
    when(studentRepository.findAll()).thenReturn(List.of(new Student(),new Student()));

    List<Student> out = studentService.getAllStudents();

    assertEquals(2,out.size());
    verify(studentRepository).findAll();
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== getStudentsBySchoolClassId =====

  @Test
  @DisplayName("getStudentsBySchoolClassId: mapuje studentów na StudentDto z UserService")
  void getStudentsBySchoolClassId_returnsDtos() {
    int classId = 1;
    Student s1 = new Student();
    s1.setUserId(5);
    Student s2 = new Student();
    s2.setUserId(6);

    User u1 = new User();
    u1.setUserId(5);
    u1.setFirstname("Anna");
    u1.setLastname("Kowalska");

    User u2 = new User();
    u2.setUserId(6);
    u2.setFirstname("Jan");
    u2.setLastname("Nowak");

    when(studentRepository.getStudentsBySchoolClassId(classId)).thenReturn(List.of(s1,s2));
    when(userService.getUserById(5)).thenReturn(u1);
    when(userService.getUserById(6)).thenReturn(u2);

    List<StudentDto> result = studentService.getStudentsBySchoolClassId(classId);

    assertEquals(2,result.size());
    assertEquals("Anna",result.get(0).getFirstName());
    assertEquals("Jan",result.get(1).getFirstName());
    verify(studentRepository).getStudentsBySchoolClassId(classId);
    verify(userService,times(2)).getUserById(anyInt());
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== getAllStudentsDto =====

  @Test
  @DisplayName("getAllStudentsDto: zwraca listę StudentDto na podstawie UserService")
  void getAllStudentsDto_returnsDtos() {
    Student s1 = new Student();
    s1.setUserId(10);
    Student s2 = new Student();
    s2.setUserId(20);

    User u1 = new User();
    u1.setUserId(10);
    u1.setFirstname("Adam");
    u1.setLastname("Kowal");

    User u2 = new User();
    u2.setUserId(20);
    u2.setFirstname("Ewa");
    u2.setLastname("Nowak");

    when(studentRepository.findAll()).thenReturn(List.of(s1,s2));
    when(userService.getUserById(10)).thenReturn(u1);
    when(userService.getUserById(20)).thenReturn(u2);

    List<StudentDto> result = studentService.getAllStudentsDto();

    assertEquals(2,result.size());
    assertEquals("Adam",result.get(0).getFirstName());
    assertEquals("Ewa",result.get(1).getFirstName());
    verify(studentRepository).findAll();
    verify(userService,times(2)).getUserById(anyInt());
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== updateStudent =====

  @Test
  @DisplayName("updateStudent: aktualizuje i zapisuje studenta")
  void updateStudent_updatesFieldsAndSaves() {
    int id = 3;
    Student existing = new Student();
    existing.setUserId(id);
    existing.setFirstname("Old");
    existing.setLastname("Name");

    Student patch = new Student();
    patch.setFirstname("New");
    patch.setLastname("Surname");
    patch.setEmail("new@example.com");
    patch.setPassword("newpass");
    patch.setParent(new Parent());
    patch.setSchoolClass(new SchoolClass());

    when(studentRepository.findById(id)).thenReturn(Optional.of(existing));
    when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

    Student out = studentService.updateStudent(id,patch);

    assertEquals("New",out.getFirstname());
    assertEquals("Surname",out.getLastname());
    assertEquals("new@example.com",out.getEmail());
    assertEquals("newpass",out.getPassword());
    assertNotNull(out.getParent());
    assertNotNull(out.getSchoolClass());
    verify(studentRepository).findById(id);
    verify(studentRepository).save(any(Student.class));
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  @Test
  @DisplayName("updateStudent: gdy brak studenta, rzuca EntityNotFoundException")
  void updateStudent_notFound_throwsException() {
    int id = 99;
    when(studentRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> studentService.updateStudent(id,new Student()));
    verify(studentRepository).findById(id);
    verify(studentRepository,never()).save(any());
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== deleteStudent =====

  @Test
  @DisplayName("deleteStudent: deleguje do deleteById")
  void deleteStudent_deletesSuccessfully() {
    int id = 5;
    studentService.deleteStudent(id);
    verify(studentRepository).deleteById(id);
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== changeStudentClass =====

  @Test
  @DisplayName("changeStudentClass: zmienia klasę ucznia i zapisuje")
  void changeStudentClass_changesAndSaves() {
    int id = 7;
    SchoolClass sc = new SchoolClass();
    Student existing = new Student();
    existing.setUserId(id);
    when(studentRepository.findById(id)).thenReturn(Optional.of(existing));
    when(studentRepository.save(existing)).thenReturn(existing);

    studentService.changeStudentClass(id,sc);

    assertEquals(sc,existing.getSchoolClass());
    verify(studentRepository).findById(id);
    verify(studentRepository).save(existing);
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  @Test
  @DisplayName("changeStudentClass: gdy brak studenta, rzuca EntityNotFoundException")
  void changeStudentClass_notFound_throwsException() {
    int id = 10;
    when(studentRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class,
        () -> studentService.changeStudentClass(id,new SchoolClass()));
    verify(studentRepository).findById(id);
    verify(studentRepository,never()).save(any());
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }

  // ===== addUserToStudents =====

  @Test
  @DisplayName("addUserToStudents: deleguje do repozytorium z poprawnymi parametrami")
  void addUserToStudents_executesSuccessfully() {
    int id = 11;
    UserDto dto = new UserDto();
    dto.setSchoolClassId(101);
    dto.setParentId(202);

    studentService.addUserToStudents(id,dto);

    verify(studentRepository).addUserToStudents(11,101,202);
    verifyNoMoreInteractions(studentRepository,remarkRepository,remarkMapper,userService);
  }
}

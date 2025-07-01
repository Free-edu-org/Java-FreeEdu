package com.byt.freeEdu.service.users;

import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.Parent;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.repository.ParentRepository;
import com.byt.freeEdu.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest{

  @InjectMocks
  private ParentService parentService;

  @Mock
  private ParentRepository parentRepository;

  @Mock
  private StudentRepository studentRepository;

  @Test
  public void addParent_savesParentSuccessfully() {
    // given
    Parent parent = new Parent();
    when(parentRepository.save(parent)).thenReturn(parent);

    // when
    Parent result = parentService.addParent(parent);

    // then
    assertNotNull(result);
    verify(parentRepository,times(1)).save(parent);
  }

  @Test
  public void getParentById_parentNotFound_throwsException() {
    // given
    int parentId = 999;
    when(parentRepository.findById(parentId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,() -> parentService.getParentById(parentId));
    verify(parentRepository,times(1)).findById(parentId);
  }

  @Test
  public void getParentById_returnsParent() {
    // given
    int parentId = 1;
    Parent parent = new Parent();
    parent.setFirstname("John");
    when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));

    // when
    Parent result = parentService.getParentById(parentId);

    // then
    assertNotNull(result);
    assertEquals("John",result.getFirstname());
    verify(parentRepository,times(1)).findById(parentId);
  }

  @Test
  public void getAllParents_returnsListOfParents() {
    // given
    Parent parent1 = new Parent();
    Parent parent2 = new Parent();
    when(parentRepository.findAll()).thenReturn(List.of(parent1,parent2));

    // when
    List<Parent> result = parentService.getAllParents();

    // then
    assertNotNull(result);
    assertEquals(2,result.size());
    verify(parentRepository,times(1)).findAll();
  }

  @Test
  public void updateParent_parentNotFound_throwsException() {
    // given
    int parentId = 999;
    Parent updatedParent = new Parent();
    when(parentRepository.findById(parentId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,
        () -> parentService.updateParent(parentId,updatedParent));
    verify(parentRepository,never()).save(any(Parent.class));
  }

  @Test
  public void deleteParent_deletesParentSuccessfully() {
    // given
    int parentId = 1;

    // when
    parentService.deleteParent(parentId);

    // then
    verify(parentRepository,times(1)).deleteById(parentId);
  }

  @Test
  public void getStudentsByParentId_returnsListOfStudents() {
    // given
    int parentId = 1;
    Student student1 = new Student();
    Student student2 = new Student();
    when(studentRepository.findByParentUserId(parentId)).thenReturn(List.of(student1,student2));

    // when
    List<Student> result = parentService.getStudentsByParentId(parentId);

    // then
    assertNotNull(result);
    assertEquals(2,result.size());
    verify(studentRepository,times(1)).findByParentUserId(parentId);
  }

  @Test
  public void addUserToParent_executesSuccessfully() {
    // given
    int parentId = 1;
    UserDto userDto = new UserDto();
    userDto.setContactInfo("123-456-789");

    // when
    parentService.addUserToParent(parentId,userDto);

    // then
    verify(parentRepository,times(1)).addUserToParents(parentId,userDto.getContactInfo());
  }
}

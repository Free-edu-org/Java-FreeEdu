package com.byt.freeEdu.service;

import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.repository.SchoolClassRepository;
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
class SchoolClassServiceTest{

  @InjectMocks
  private SchoolClassService schoolClassService;

  @Mock
  private SchoolClassRepository schoolClassRepository;

  @Test
  public void getAllSchoolClass_returnsListOfSchoolClasses(){
    // given
    SchoolClass schoolClass1 = new SchoolClass("Class A");
    SchoolClass schoolClass2 = new SchoolClass("Class B");
    when(schoolClassRepository.findAll()).thenReturn(List.of(schoolClass1,schoolClass2));

    // when
    List<SchoolClass> schoolClasses = schoolClassService.getAllSchoolClass();

    // then
    assertNotNull(schoolClasses);
    assertEquals(2,schoolClasses.size());
    verify(schoolClassRepository,times(1)).findAll();
  }

  @Test
  public void getSchoolClassById_classNotFound_throwsException(){
    // given
    int classId = 999;
    when(schoolClassRepository.findById(classId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,
        () -> schoolClassService.getSchoolClassById(classId));
  }

  @Test
  public void getSchoolClassById_returnsSchoolClass(){
    // given
    int classId = 1;
    SchoolClass schoolClass = new SchoolClass("Class A");
    when(schoolClassRepository.findById(classId)).thenReturn(Optional.of(schoolClass));

    // when
    SchoolClass result = schoolClassService.getSchoolClassById(classId);

    // then
    assertNotNull(result);
    assertEquals("Class A",result.getName());
    verify(schoolClassRepository,times(1)).findById(classId);
  }

  @Test
  public void getSchoolClassByName_classNotFound_throwsException(){
    // given
    String className = "Non-existent Class";
    when(schoolClassRepository.findByName(className)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,
        () -> schoolClassService.getSchoolClassByName(className));
  }

  @Test
  public void getSchoolClassByName_returnsSchoolClass(){
    // given
    String className = "Class A";
    SchoolClass schoolClass = new SchoolClass(className);
    when(schoolClassRepository.findByName(className)).thenReturn(Optional.of(schoolClass));

    // when
    SchoolClass result = schoolClassService.getSchoolClassByName(className);

    // then
    assertNotNull(result);
    assertEquals(className,result.getName());
    verify(schoolClassRepository,times(1)).findByName(className);
  }

  @Test
  public void addSchoolClass_emptyName_throwsException(){
    // when & then
    assertThrows(IllegalArgumentException.class,() -> schoolClassService.addSchoolClass(" "));
    verify(schoolClassRepository,never()).save(any(SchoolClass.class));
  }

  @Test
  public void addSchoolClass_savesSchoolClass(){
    // given
    String className = "Class A";
    SchoolClass schoolClass = new SchoolClass(className);
    when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);

    // when
    SchoolClass result = schoolClassService.addSchoolClass(className);

    // then
    assertNotNull(result);
    assertEquals(className,result.getName());
    verify(schoolClassRepository,times(1)).save(any(SchoolClass.class));
  }

  @Test
  public void deleteSchoolClassById_classNotFound_throwsException(){
    // given
    int classId = 999;
    when(schoolClassRepository.findById(classId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,
        () -> schoolClassService.deleteSchoolClassById(classId));
    verify(schoolClassRepository,never()).delete(any(SchoolClass.class));
  }

  @Test
  public void deleteSchoolClassById_deletesSchoolClass(){
    // given
    int classId = 1;
    SchoolClass schoolClass = new SchoolClass("Class A");
    when(schoolClassRepository.findById(classId)).thenReturn(Optional.of(schoolClass));

    // when
    schoolClassService.deleteSchoolClassById(classId);

    // then
    verify(schoolClassRepository,times(1)).delete(schoolClass);
  }

  @Test
  public void deleteSchoolClassByName_classNotFound_throwsException(){
    // given
    String className = "Non-existent Class";
    when(schoolClassRepository.findByName(className)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,
        () -> schoolClassService.deleteSchoolClassByName(className));
    verify(schoolClassRepository,never()).delete(any(SchoolClass.class));
  }

  @Test
  public void deleteSchoolClassByName_deletesSchoolClass(){
    // given
    String className = "Class A";
    SchoolClass schoolClass = new SchoolClass(className);
    when(schoolClassRepository.findByName(className)).thenReturn(Optional.of(schoolClass));

    // when
    schoolClassService.deleteSchoolClassByName(className);

    // then
    verify(schoolClassRepository,times(1)).delete(schoolClass);
  }

  @Test
  public void getAllClassesWithStudentCount_returnsClassesWithCounts(){
    // given
    SchoolClass schoolClass = new SchoolClass("Class A");
    schoolClass.setSchoolClassId(1);
    when(schoolClassRepository.findAll()).thenReturn(List.of(schoolClass));
    when(schoolClassRepository.countStudentsInClass(1)).thenReturn(10L);

    // when
    List<SchoolClass> results = schoolClassService.getAllClassesWithStudentCount();

    // then
    assertNotNull(results);
    assertEquals(1,results.size());
    assertEquals(10L,results.get(0).getStudentCount());
    verify(schoolClassRepository,times(1)).countStudentsInClass(1);
  }
}

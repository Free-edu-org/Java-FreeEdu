package com.byt.freeEdu.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.repository.SchoolClassRepository;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchoolClassServiceTest{

  @InjectMocks
  private SchoolClassService schoolClassService;

  @Mock
  private SchoolClassRepository schoolClassRepository;

  @Test
  void getAllSchoolClass_returnsListOfSchoolClasses() {
    SchoolClass c1 = new SchoolClass("Class A");
    SchoolClass c2 = new SchoolClass("Class B");
    when(schoolClassRepository.findAll()).thenReturn(List.of(c1,c2));

    List<SchoolClass> out = schoolClassService.getAllSchoolClass();

    assertNotNull(out);
    assertEquals(2,out.size());
    verify(schoolClassRepository).findAll();
  }

  @Test
  void getSchoolClassById_classNotFound_throwsException() {
    int id = 999;
    when(schoolClassRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> schoolClassService.getSchoolClassById(id));
  }

  @Test
  void getSchoolClassById_returnsSchoolClass() {
    int id = 1;
    SchoolClass sc = new SchoolClass("Class A");
    when(schoolClassRepository.findById(id)).thenReturn(Optional.of(sc));

    SchoolClass out = schoolClassService.getSchoolClassById(id);

    assertNotNull(out);
    assertEquals("Class A",out.getName());
    verify(schoolClassRepository).findById(id);
  }

  @Test
  void getSchoolClassByName_classNotFound_throwsException() {
    String name = "Nope";
    when(schoolClassRepository.findByName(name)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> schoolClassService.getSchoolClassByName(name));
  }

  @Test
  void getSchoolClassByName_returnsSchoolClass() {
    String name = "Class A";
    SchoolClass sc = new SchoolClass(name);
    when(schoolClassRepository.findByName(name)).thenReturn(Optional.of(sc));

    SchoolClass out = schoolClassService.getSchoolClassByName(name);

    assertNotNull(out);
    assertEquals(name,out.getName());
    verify(schoolClassRepository).findByName(name);
  }

  @Test
  void addSchoolClass_emptyName_throwsException() {
    assertThrows(IllegalArgumentException.class,() -> schoolClassService.addSchoolClass(" "));
    verify(schoolClassRepository,never()).save(any(SchoolClass.class));
  }

  @Test
  void addSchoolClass_savesSchoolClass() {
    String name = "Class A";
    SchoolClass sc = new SchoolClass(name);
    when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(sc);

    SchoolClass out = schoolClassService.addSchoolClass(name);

    assertNotNull(out);
    assertEquals(name,out.getName());
    verify(schoolClassRepository).save(any(SchoolClass.class));
  }

  @Test
  void deleteSchoolClassById_classNotFound_throwsException() {
    int id = 999;
    when(schoolClassRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> schoolClassService.deleteSchoolClassById(id));
    verify(schoolClassRepository,never()).delete(any(SchoolClass.class));
  }

  @Test
  void deleteSchoolClassById_deletesSchoolClass() {
    int id = 1;
    SchoolClass sc = new SchoolClass("Class A");
    when(schoolClassRepository.findById(id)).thenReturn(Optional.of(sc));

    schoolClassService.deleteSchoolClassById(id);

    verify(schoolClassRepository).delete(sc);
  }

  @Test
  void deleteSchoolClassByName_classNotFound_throwsException() {
    String name = "Nope";
    when(schoolClassRepository.findByName(name)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> schoolClassService.deleteSchoolClassByName(name));
    verify(schoolClassRepository,never()).delete(any(SchoolClass.class));
  }

  @Test
  void deleteSchoolClassByName_deletesSchoolClass() {
    String name = "Class A";
    SchoolClass sc = new SchoolClass(name);
    when(schoolClassRepository.findByName(name)).thenReturn(Optional.of(sc));

    schoolClassService.deleteSchoolClassByName(name);

    verify(schoolClassRepository).delete(sc);
  }

  @Test
  void getAllClassesWithStudentCount_returnsClassesWithCounts() {
    SchoolClass sc = new SchoolClass("Class A");
    sc.setSchoolClassId(1);
    when(schoolClassRepository.findAll()).thenReturn(List.of(sc));
    when(schoolClassRepository.countStudentsInClass(1)).thenReturn(10L);

    List<SchoolClass> out = schoolClassService.getAllClassesWithStudentCount();

    assertNotNull(out);
    assertEquals(1,out.size());
    assertEquals(10L,out.get(0).getStudentCount());
    verify(schoolClassRepository).findAll();
    verify(schoolClassRepository).countStudentsInClass(1);
  }

  @Test
  void updateSchoolClass_updatesNameAndSaves() {
    int id = 3;
    SchoolClass sc = new SchoolClass("Old");
    when(schoolClassRepository.findById(id)).thenReturn(Optional.of(sc));
    when(schoolClassRepository.save(sc)).thenReturn(sc);

    schoolClassService.updateSchoolClass(id,"New");

    assertEquals("New",sc.getName());
    verify(schoolClassRepository).findById(id);
    verify(schoolClassRepository).save(sc);
  }

  @Test
  void updateSchoolClass_notFound_throwsException() {
    int id = 404;
    when(schoolClassRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> schoolClassService.updateSchoolClass(id,"X"));
    verify(schoolClassRepository,never()).save(any());
  }
}

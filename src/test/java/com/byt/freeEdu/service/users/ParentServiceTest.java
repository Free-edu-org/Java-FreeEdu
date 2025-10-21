package com.byt.freeEdu.service.users;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.byt.freeEdu.model.users.Parent;
import com.byt.freeEdu.repository.ParentRepository;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest{

  @InjectMocks
  private ParentService parentService;

  @Mock
  private ParentRepository parentRepository;

  @Test
  @DisplayName("addParent: zapisuje i zwraca rodzica")
  void addParent_savesParentSuccessfully() {
    Parent p = new Parent();
    p.setFirstname("Jan");
    when(parentRepository.save(any(Parent.class))).thenAnswer(inv -> inv.getArgument(0));

    Parent out = parentService.addParent(p);

    assertNotNull(out);
    assertEquals("Jan",out.getFirstname());
    verify(parentRepository).save(any(Parent.class));
    verifyNoMoreInteractions(parentRepository);
  }

  @Test
  @DisplayName("getParentById: gdy brak, rzuca EntityNotFoundException")
  void getParentById_parentNotFound_throwsException() {
    int id = 999;
    when(parentRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> parentService.getParentById(id));
    verify(parentRepository).findById(id);
    verifyNoMoreInteractions(parentRepository);
  }

  @Test
  @DisplayName("getParentById: zwraca rodzica")
  void getParentById_returnsParent() {
    int id = 1;
    Parent p = new Parent();
    p.setFirstname("Anna");
    when(parentRepository.findById(id)).thenReturn(Optional.of(p));

    Parent out = parentService.getParentById(id);

    assertNotNull(out);
    assertEquals("Anna",out.getFirstname());
    verify(parentRepository).findById(id);
    verifyNoMoreInteractions(parentRepository);
  }

  @Test
  @DisplayName("getAllParents: zwraca listę rodziców")
  void getAllParents_returnsListOfParents() {
    when(parentRepository.findAll()).thenReturn(List.of(new Parent(),new Parent()));

    List<Parent> out = parentService.getAllParents();

    assertNotNull(out);
    assertEquals(2,out.size());
    verify(parentRepository).findAll();
    verifyNoMoreInteractions(parentRepository);
  }

  @Test
  @DisplayName("updateParent: gdy brak rodzica, rzuca EntityNotFoundException")
  void updateParent_parentNotFound_throwsException() {
    int id = 999;
    when(parentRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> parentService.updateParent(id,new Parent()));
    verify(parentRepository).findById(id);
    verify(parentRepository,never()).save(any());
    verifyNoMoreInteractions(parentRepository);
  }

  @Test
  @DisplayName("updateParent: nadpisanie null-ami powoduje NPE (model non-null)")
  void updateParent_overwritesWithNulls_throwsNpe() {
    int id = 1;
    Parent existing = new Parent();
    existing.setUserId(id);
    existing.setFirstname("X");
    existing.setLastname("Y");
    existing.setEmail("x@y");
    existing.setPassword("p");
    existing.setContactInfo("111-222");

    Parent patch = new Parent();

    when(parentRepository.findById(id)).thenReturn(Optional.of(existing));

    assertThrows(NullPointerException.class,() -> parentService.updateParent(id,patch));
    verify(parentRepository).findById(id);
    verify(parentRepository,never()).save(any());
    verifyNoMoreInteractions(parentRepository);
  }

  @Test
  @DisplayName("updateParent: aktualizuje i zapisuje pola")
  void updateParent_updatesAndSaves() {
    int id = 2;
    Parent existing = new Parent();
    existing.setUserId(id);
    existing.setFirstname("Old");
    existing.setLastname("Name");
    existing.setEmail("old@ex");
    existing.setPassword("old");
    existing.setContactInfo("000-000");

    Parent patch = new Parent();
    patch.setFirstname("New");
    patch.setLastname("Surname");
    patch.setEmail("new@ex");
    patch.setPassword("new");
    patch.setContactInfo("123-456");

    when(parentRepository.findById(id)).thenReturn(Optional.of(existing));
    when(parentRepository.save(any(Parent.class))).thenAnswer(inv -> inv.getArgument(0));

    Parent out = parentService.updateParent(id,patch);

    assertEquals("New",out.getFirstname());
    assertEquals("Surname",out.getLastname());
    assertEquals("new@ex",out.getEmail());
    assertEquals("new",out.getPassword());
    assertEquals("123-456",out.getContactInfo());
    verify(parentRepository).findById(id);
    verify(parentRepository).save(any(Parent.class));
    verifyNoMoreInteractions(parentRepository);
  }

  @Test
  @DisplayName("deleteParent: deleguje do deleteById")
  void deleteParent_deletesParentSuccessfully() {
    int id = 1;
    parentService.deleteParent(id);
    verify(parentRepository).deleteById(id);
    verifyNoMoreInteractions(parentRepository);
  }
}

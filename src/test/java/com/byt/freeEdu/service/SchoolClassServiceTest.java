package com.byt.freeEdu.service;

import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.repository.SchoolClassRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolClassServiceTest {

    @InjectMocks
    private SchoolClassService schoolClassService;

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Test
    public void getAllSchoolClass_returnsAllClasses() {
        //given
        SchoolClass class1 = new SchoolClass("Math101");
        SchoolClass class2 = new SchoolClass("Science102");

        when(schoolClassRepository.findAll()).thenReturn(List.of(class1, class2));

        //when
        List<SchoolClass> schoolClasses = schoolClassService.getAllSchoolClass();

        //then
        assertNotNull(schoolClasses);
        assertEquals(2, schoolClasses.size());
        assertEquals("Math101", schoolClasses.get(0).getName());
        assertEquals("Science102", schoolClasses.get(1).getName());
    }

    @Test
    public void getSchoolClassById_classFound_returnsClass() {
        //given
        int classId = 0;
        SchoolClass schoolClass = new SchoolClass("Math101");

        when(schoolClassRepository.findById(classId)).thenReturn(Optional.of(schoolClass));

        //when
        SchoolClass foundClass = schoolClassService.getSchoolClassById(classId);

        //then
        assertNotNull(foundClass);
        assertEquals(classId, foundClass.getSchoolClassId());
    }

    @Test
    public void getSchoolClassById_classNotFound_throwsException() {
        //given
        int classId = 999;

        when(schoolClassRepository.findById(classId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> schoolClassService.getSchoolClassById(classId));
    }

    @Test
    public void getSchoolClassByName_classFound_returnsClass() {
        //given
        String className = "Math101";
        SchoolClass schoolClass = new SchoolClass(className);

        when(schoolClassRepository.findByName(className)).thenReturn(Optional.of(schoolClass));

        //when
        SchoolClass foundClass = schoolClassService.getSchoolClassByName(className);

        //then
        assertNotNull(foundClass);
        assertEquals(className, foundClass.getName());
    }

    @Test
    public void getSchoolClassByName_classNotFound_throwsException() {
        //given
        String className = "UnknownClass";

        when(schoolClassRepository.findByName(className)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> schoolClassService.getSchoolClassByName(className));
    }

    @Test
    public void addSchoolClass_validClass_createsClass() {
        //given
        String className = "Math101";
        SchoolClass schoolClass = new SchoolClass(className);

        when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);

        //when
        SchoolClass createdClass = schoolClassService.addSchoolClass(className);

        //then
        assertNotNull(createdClass);
        assertEquals(className, createdClass.getName());
        verify(schoolClassRepository, times(1)).save(any(SchoolClass.class));
    }

    @Test
    public void addSchoolClass_invalidName_throwsException() {
        //given
        String invalidClassName = "";

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> schoolClassService.addSchoolClass(invalidClassName));

        assertEquals("Class name cannot be empty", exception.getMessage());
        verify(schoolClassRepository, times(0)).save(any(SchoolClass.class));
    }

    @Test
    public void deleteSchoolClassById_classFound_deletesClass() {
        //given
        int classId = 1;
        SchoolClass schoolClass = new SchoolClass("Math101");

        when(schoolClassRepository.findById(classId)).thenReturn(Optional.of(schoolClass));

        //when
        schoolClassService.deleteSchoolClassById(classId);

        //then
        verify(schoolClassRepository, times(1)).delete(any(SchoolClass.class));
    }

    @Test
    public void deleteSchoolClassById_classNotFound_throwsException() {
        //given
        int classId = 999;

        when(schoolClassRepository.findById(classId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> schoolClassService.deleteSchoolClassById(classId));
    }

    @Test
    public void deleteSchoolClassByName_classFound_deletesClass() {
        //given
        String className = "Math101";
        SchoolClass schoolClass = new SchoolClass(className);

        when(schoolClassRepository.findByName(className)).thenReturn(Optional.of(schoolClass));

        //when
        schoolClassService.deleteSchoolClassByName(className);

        //then
        verify(schoolClassRepository, times(1)).delete(any(SchoolClass.class));
    }

    @Test
    public void deleteSchoolClassByName_classNotFound_throwsException() {
        //given
        String className = "UnknownClass";

        when(schoolClassRepository.findByName(className)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> schoolClassService.deleteSchoolClassByName(className));
    }

    @ParameterizedTest
    @MethodSource("invalidClassNames")
    public void addSchoolClass_invalidName_throwsException(String className, String expectedMessage) {
        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> schoolClassService.addSchoolClass(className));
        assertEquals(expectedMessage, exception.getMessage());
        verify(schoolClassRepository, times(0)).save(any(SchoolClass.class));
    }

    private static Stream<Arguments> invalidClassNames() {
        return Stream.of(
                Arguments.of("", "Class name cannot be empty"),
                Arguments.of("   ", "Class name cannot be empty"),
                Arguments.of(null, "Class name cannot be empty")
        );
    }
}

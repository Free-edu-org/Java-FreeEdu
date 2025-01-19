package com.byt.freeEdu.service;

import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.RemarkRepository;
import com.byt.freeEdu.repository.StudentRepository;
import com.byt.freeEdu.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemarkServiceTest {

    @InjectMocks
    private RemarkService remarkService;

    @Mock
    private RemarkRepository remarkRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;

    @ParameterizedTest
    @MethodSource("emptyContent")
    public void addRemark_emptyContent_throwsException(String content) {
        //given
        int studentId = 1;
        int teacherId = 1;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                remarkService.addRemark(content, studentId, teacherId)
        );

        //then
        assertEquals("Content cannot be empty", exception.getMessage());
        verify(remarkRepository, times(0)).save(any(Remark.class));
    }

    @Test
    public void addRemark_successfully_savesRemark() {
        //given
        String content = "Great student!";
        int studentId = 1;
        int teacherId = 1;

        Student student = new Student();
        student.setUserId(studentId);

        Teacher teacher = new Teacher();
        teacher.setUserId(teacherId);

        Remark remark = new Remark(content, LocalDate.now(), student, teacher);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(remarkRepository.save(any(Remark.class))).thenReturn(remark);

        //when
        Remark savedRemark = remarkService.addRemark(content, studentId, teacherId);

        //then
        assertNotNull(savedRemark);
        assertEquals(content, savedRemark.getContent());
        verify(remarkRepository, times(1)).save(any(Remark.class));
    }

    @Test
    public void updateRemark_successfully_updatesRemark() {
        //given
        int remarkId = 1;
        Remark existingRemark = new Remark("Old remark", LocalDate.now(), new Student(), new Teacher());
        Remark updatedRemark = new Remark("Updated remark", LocalDate.now(), new Student(), new Teacher());

        when(remarkRepository.findById(remarkId)).thenReturn(Optional.of(existingRemark));
        when(remarkRepository.save(any(Remark.class))).thenReturn(updatedRemark);

        //when
        Remark savedRemark = remarkService.updateRemark(remarkId, updatedRemark);

        //then
        assertNotNull(savedRemark);
        assertEquals("Updated remark", savedRemark.getContent());
        verify(remarkRepository, times(1)).save(any(Remark.class));
    }

    @Test
    public void updateRemark_remarkNotFound_throwsException() {
        //given
        int remarkId = 999;
        Remark updatedRemark = new Remark("Updated remark", LocalDate.now(), new Student(), new Teacher());

        when(remarkRepository.findById(remarkId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> remarkService.updateRemark(remarkId, updatedRemark));
        verify(remarkRepository, times(0)).save(any(Remark.class));
    }

    @Test
    public void deleteRemark_remarkNotFound_throwsException() {
        //given
        int remarkId = 999;

        when(remarkRepository.findById(remarkId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> remarkService.deleteRemark(remarkId));
        verify(remarkRepository, times(0)).delete(any(Remark.class));
    }

    @Test
    public void deleteRemark_successfully_deletesRemark() {
        //given
        int remarkId = 1;
        Remark remark = new Remark("Some remark", LocalDate.now(), new Student(), new Teacher());

        when(remarkRepository.findById(remarkId)).thenReturn(Optional.of(remark));

        //when
        remarkService.deleteRemark(remarkId);

        //then
        verify(remarkRepository, times(1)).delete(any(Remark.class));
    }

    @Test
    public void getRemarksByStudentId_studentNotFound_throwsException() {
        //given
        int studentId = 999;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> remarkService.getRemarksByStudentId(studentId));
    }

    private static Stream<Arguments> emptyContent() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of(" "),
                Arguments.of((Object) null)
        );
    }
}

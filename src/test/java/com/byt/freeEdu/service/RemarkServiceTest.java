package com.byt.freeEdu.service;

import com.byt.freeEdu.mapper.RemarkMapper;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.RemarkRepository;
import com.byt.freeEdu.repository.StudentRepository;
import com.byt.freeEdu.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    @Mock
    private RemarkMapper remarkMapper;

    @Test
    public void getAllRemarks_returnsMappedRemarkDtos() {
        //given
        Remark remark = new Remark("Test content", LocalDate.now(), new Student(), new Teacher());
        when(remarkRepository.findAll()).thenReturn(List.of(remark));
        when(remarkMapper.toDto(any(Remark.class))).thenReturn(new RemarkDto());

        //when
        List<RemarkDto> remarks = remarkService.getAllRemarks();

        //then
        assertNotNull(remarks);
        assertEquals(1, remarks.size());
        verify(remarkRepository, times(1)).findAll();
    }

    @Test
    public void getRemarkById_remarkNotFound_throwsException() {
        //given
        int remarkId = 999;
        when(remarkRepository.findById(remarkId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> remarkService.getRemarkById(remarkId));
    }

    @Test
    public void getRemarkById_returnsRemarkDto() {
        //given
        int remarkId = 1;
        Remark remark = new Remark("Content", LocalDate.now(), new Student(), new Teacher());
        when(remarkRepository.findById(remarkId)).thenReturn(Optional.of(remark));
        when(remarkMapper.toDto(remark)).thenReturn(new RemarkDto());

        //when
        RemarkDto result = remarkService.getRemarkById(remarkId);

        //then
        assertNotNull(result);
        verify(remarkRepository, times(1)).findById(remarkId);
    }

    @Test
    public void addRemark_emptyContent_throwsException() {
        //given
        String content = " ";
        int studentId = 1;
        int teacherId = 1;

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                remarkService.addRemark(content, studentId, teacherId));
        assertEquals("Content cannot be empty", exception.getMessage());
        verify(remarkRepository, never()).save(any());
    }

    @Test
    public void addRemark_studentOrTeacherNotFound_throwsException() {
        //given
        String content = "Remark content";
        int studentId = 1;
        int teacherId = 1;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> remarkService.addRemark(content, studentId, teacherId));
    }

    @Test
    public void addRemark_successfullySavesRemark() {
        //given
        String content = "Remark content";
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
        Remark result = remarkService.addRemark(content, studentId, teacherId);

        //then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        verify(remarkRepository, times(1)).save(any(Remark.class));
    }

    @Test
    public void deleteRemark_remarkNotFound_throwsException() {
        //given
        int remarkId = 999;
        when(remarkRepository.findById(remarkId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> remarkService.deleteRemark(remarkId));
        verify(remarkRepository, never()).delete(any());
    }

    @Test
    public void deleteRemark_successfullyDeletesRemark() {
        //given
        int remarkId = 1;
        Remark remark = new Remark("Test", LocalDate.now(), new Student(), new Teacher());

        when(remarkRepository.findById(remarkId)).thenReturn(Optional.of(remark));

        //when
        remarkService.deleteRemark(remarkId);

        //then
        verify(remarkRepository, times(1)).delete(remark);
    }

    @Test
    public void getRemarksByStudentId_studentNotFound_throwsException() {
        //given
        int studentId = 999;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> remarkService.getRemarksByStudentId(studentId));
    }

    @Test
    public void getRemarksByStudentId_returnsMappedRemarkDtos() {
        //given
        int studentId = 1;
        Student student = new Student();
        student.setUserId(studentId);

        Remark remark = new Remark("Content", LocalDate.now(), student, new Teacher());
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(remarkRepository.findByStudent(student)).thenReturn(List.of(remark));
        when(remarkMapper.toDto(any(Remark.class))).thenReturn(new RemarkDto());

        //when
        List<RemarkDto> results = remarkService.getRemarksByStudentId(studentId);

        //then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(remarkRepository, times(1)).findByStudent(student);
    }
}

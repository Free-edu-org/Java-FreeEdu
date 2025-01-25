package com.byt.freeEdu.service;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.enums.AttendanceEnum;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.repository.AttendanceRepository;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public AttendanceService(AttendanceRepository attendanceRepository, StudentService studentService, TeacherService teacherService) {
        this.attendanceRepository = attendanceRepository;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    public List<Attendance> getAttendancesForStudent(int studentId) {
        return attendanceRepository.findByStudent_UserId(studentId);
    }

    public void saveAttendance(Attendance attendance) {
        attendanceRepository.save(attendance);
    }

    public Attendance getAttendanceById(int id) {
        return attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found with ID: " + id));
    }

    public void markAttendance(Map<Integer, AttendanceEnum> attendanceMap, SubjectEnum subject, int teacherId) {
        attendanceMap.forEach((studentId, status) -> {
            Attendance attendance = new Attendance();
            attendance.setStudent(studentService.getStudentById(studentId));
            attendance.setTeacher(teacherService.getTeacherById(teacherId));
            attendance.setAttendanceDate(LocalDate.now());
            attendance.setStatus(status);
            attendance.setSubject(subject);

            attendanceRepository.save(attendance);
        });
    }

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Attendance updateAttendance(int id, Attendance updatedAttendance) {
        Attendance existingAttendance = attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found with ID: " + id));
        existingAttendance.setStudent(updatedAttendance.getStudent());
        existingAttendance.setAttendanceDate(updatedAttendance.getAttendanceDate());
        existingAttendance.setStatus(updatedAttendance.getStatus());
        existingAttendance.setTeacher(updatedAttendance.getTeacher());

        return attendanceRepository.save(existingAttendance);
    }

    public void deleteAttendance(int id) {
        attendanceRepository.deleteById(id);
    }
}
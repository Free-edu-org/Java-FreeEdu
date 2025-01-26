package com.byt.freeEdu.service;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.AttendanceDto;
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
import java.util.stream.Collectors;

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

    public AttendanceDto getAttendanceByIdAdmin(int attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono obecności o ID " + attendanceId));
        return AttendanceDto.fromEntity(attendance);
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

    public List<AttendanceDto> getAllAttendancesAdmin() {
        List<Attendance> attendances = attendanceRepository.findAll();

        return attendances.stream()
                .map(AttendanceDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Attendance updateAttendance(int id, Attendance updatedAttendance) {
        Attendance existingAttendance = attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found with ID: " + id));
        existingAttendance.setStudent(updatedAttendance.getStudent());
        existingAttendance.setAttendanceDate(updatedAttendance.getAttendanceDate());
        existingAttendance.setStatus(updatedAttendance.getStatus());
        existingAttendance.setTeacher(updatedAttendance.getTeacher());

        return attendanceRepository.save(existingAttendance);
    }

    public void updateAttendanceAdmin(int attendanceId, AttendanceDto attendanceDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono obecności o ID " + attendanceId));

        attendance.setStatus(AttendanceEnum.valueOf(attendanceDto.getAttendance_status()));
        attendance.setAttendanceDate(LocalDate.parse(attendanceDto.getAttendanceDate()));
        attendanceRepository.save(attendance);
    }

    public void deleteAttendance(int id) {
        attendanceRepository.deleteById(id);
    }
}
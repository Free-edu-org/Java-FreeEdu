package com.byt.freeEdu.service;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.repository.AttendanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public void saveAttendance(Attendance attendance) {
        attendanceRepository.save(attendance);
    }

    public Attendance getAttendanceById(int id) {
        return attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found with ID: " + id));
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

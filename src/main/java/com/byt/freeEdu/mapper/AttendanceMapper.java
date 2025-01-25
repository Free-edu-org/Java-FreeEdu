package com.byt.freeEdu.mapper;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.AttendanceDto;
import com.byt.freeEdu.model.DTO.AttendanceFormDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AttendanceMapper {

    // Metoda do mapowania Attendance na AttendanceFormDto (istniejąca)
    default AttendanceFormDto toDto(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        AttendanceFormDto attendanceFormDto = new AttendanceFormDto();
        attendanceFormDto.setAttendanceMap(Collections.singletonMap(attendance.getAttendanceId(), attendance.getStatus()));
        attendanceFormDto.setGlobalSubject(attendance.getSubject());
        attendanceFormDto.setAttendanceDate(attendance.getAttendanceDate());

        return attendanceFormDto;
    }

    // Metoda do mapowania listy Attendance na listę AttendanceFormDto (istniejąca)
    default List<AttendanceFormDto> toDtoList(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return Collections.emptyList();
        }

        return attendances.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Nowa metoda do mapowania Attendance na AttendanceDto
    default AttendanceDto toAttendanceDto(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setAttendanceId(attendance.getAttendanceId());
        attendanceDto.setStudentId(attendance.getStudent().getUserId());
        attendanceDto.setTeacherId(attendance.getTeacher().getUserId());
        attendanceDto.setAttendanceDate(attendance.getAttendanceDate());
        attendanceDto.setStatus(attendance.getStatus());
        attendanceDto.setSubject(attendance.getSubject());

        return attendanceDto;
    }

    // Nowa metoda do mapowania listy Attendance na listę AttendanceDto
    default List<AttendanceDto> toAttendanceDtoList(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return Collections.emptyList();
        }

        return attendances.stream()
                .map(this::toAttendanceDto)
                .collect(Collectors.toList());
    }
}
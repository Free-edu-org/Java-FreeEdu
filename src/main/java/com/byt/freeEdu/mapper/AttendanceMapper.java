package com.byt.freeEdu.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.AttendanceDto;
import com.byt.freeEdu.model.DTO.AttendanceFormDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AttendanceMapper {

    default AttendanceFormDto toDto(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        AttendanceFormDto attendanceFormDto = new AttendanceFormDto();
        attendanceFormDto.setAttendanceMap(
                Collections.singletonMap(attendance.getAttendanceId(), attendance.getStatus()));
        attendanceFormDto.setGlobalSubject(attendance.getSubject());
        attendanceFormDto.setAttendanceDate(attendance.getAttendanceDate());

        return attendanceFormDto;
    }

    default List<AttendanceFormDto> toDtoList(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return Collections.emptyList();
        }

        return attendances.stream().map(this::toDto).collect(Collectors.toList());
    }

    default AttendanceDto toAttendanceDto(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setAttendanceId(attendance.getAttendanceId());

        attendanceDto.setStudentId(attendance.getStudent().getUserId());
        attendanceDto.setStudentFirstName(attendance.getStudent().getFirstname());
        attendanceDto.setStudentLastName(attendance.getStudent().getLastname());

        attendanceDto.setTeacherId(attendance.getTeacher().getUserId());
        attendanceDto.setTeacherFirstName(attendance.getTeacher().getFirstname());
        attendanceDto.setTeacherLastName(attendance.getTeacher().getLastname());

        attendanceDto.setAttendanceDate(attendance.getAttendanceDate().toString());
        attendanceDto.setAttendanceStatus(attendance.getStatus().getDisplayName());
        attendanceDto.setSubjectEnum(attendance.getSubject());
        attendanceDto.setSubjectName(attendance.getSubject().getDisplayName());

        return attendanceDto;
    }

    default List<AttendanceDto> toAttendanceDtoList(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return Collections.emptyList();
        }

        return attendances.stream().map(this::toAttendanceDto).collect(Collectors.toList());
    }
}

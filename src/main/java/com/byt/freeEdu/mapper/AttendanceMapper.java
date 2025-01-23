package com.byt.freeEdu.mapper;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.AttendanceFormDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AttendanceMapper {

    default AttendanceFormDto toDto(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        AttendanceFormDto attendanceFormDto = new AttendanceFormDto();
        attendanceFormDto.setAttendanceMap(Collections.singletonMap(attendance.getAttendanceId(), attendance.getStatus()));

        return attendanceFormDto;
    }

    default List<AttendanceFormDto> toDtoList(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return Collections.emptyList();
        }

        return attendances.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

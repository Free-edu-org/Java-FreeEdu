package com.byt.freeEdu.mapper;

import com.byt.freeEdu.model.DTO.ScheduleAdminDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.Schedule;
import org.mapstruct.Builder;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ScheduleMapper {

    @Mapping(target = "className", source = "schoolClass.name")
    @Mapping(target = "teacherFullName", expression = "java(schedule.getTeacher().getFirstname() + \" \" + schedule.getTeacher().getLastname())")
    ScheduleDto toDto(Schedule schedule);

    @Mapping(target = "id", source = "scheduleId")
    @Mapping(target = "className", source = "schoolClass.name")
    @Mapping(target = "teacherFullName", expression = "java(schedule.getTeacher().getFirstname() + \" \" + schedule.getTeacher().getLastname())")
    ScheduleAdminDto toAdminDto(Schedule schedule);
}

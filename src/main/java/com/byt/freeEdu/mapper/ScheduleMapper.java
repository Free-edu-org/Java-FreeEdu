package com.byt.freeEdu.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.byt.freeEdu.model.DTO.ScheduleAdminDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.enums.SubjectEnum;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring", builder = @Builder(disableBuilder = true), nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ScheduleMapper {

    @Mapping(target = "id", source = "scheduleId")
    @Mapping(target = "className", source = "schoolClass.name")
    @Mapping(target = "teacherId", expression = "java(schedule.getTeacher().getUserId())")
    @Mapping(target = "teacherFirstName", expression = "java(schedule.getTeacher().getFirstname())")
    @Mapping(target = "teacherLastName", expression = "java(schedule.getTeacher().getLastname())")
    @Mapping(target = "subjectName", source = "subject", qualifiedByName = "translateSubject")
    ScheduleDto toDto(Schedule schedule);

    @Mapping(target = "id", source = "scheduleId")
    @Mapping(target = "className", source = "schoolClass.name")
    @Mapping(target = "subjectName", source = "subject", qualifiedByName = "translateSubject")
    @Mapping(target = "teacherId", expression = "java(schedule.getTeacher().getUserId())")
    @Mapping(target = "teacherFirstName", expression = "java(schedule.getTeacher().getFirstname())")
    @Mapping(target = "teacherLastName", expression = "java(schedule.getTeacher().getLastname())")
    ScheduleAdminDto toAdminDto(Schedule schedule);

    @Named("translateSubject")
    default String translateSubject(SubjectEnum subject) {
        if (subject == null) {
            return "Nieznany przedmiot";
        }
        switch (subject) {
            case POLISH:
                return "Język polski";
            case MATH:
                return "Matematyka";
            case GEOGRAPHY:
                return "Geografia";
            case HISTORY:
                return "Historia";
            case SCIENCE:
                return "Nauki ścisłe";
            case ART:
                return "Sztuka";
            case SPORTS:
                return "Wychowanie fizyczne";
            default:
                return "Nieznany przedmiot";
        }
    }
}

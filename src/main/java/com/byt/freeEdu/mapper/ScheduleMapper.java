package com.byt.freeEdu.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.byt.freeEdu.model.DTO.ScheduleAdminDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.enums.SubjectEnum;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring", builder = @Builder(disableBuilder = true), nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ScheduleMapper{

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

  @Mapping(target = "scheduleId", source = "id")
  @Mapping(target = "subject", source = "subjectName", qualifiedByName = "mapSubject")
  @Mapping(target = "teacher", ignore = true)
  @Mapping(target = "schoolClass", ignore = true)
  Schedule toEntity(ScheduleDto dto);

  @Mapping(target = "scheduleId", ignore = true)
  @Mapping(target = "subject", source = "subjectName", qualifiedByName = "mapSubject")
  @Mapping(target = "teacher", ignore = true)
  @Mapping(target = "schoolClass", ignore = true)
  void updateEntityFromDto(ScheduleDto dto, @MappingTarget Schedule entity);

  @Named("mapSubject")
  default SubjectEnum mapSubject(String subjectName) {
    if (subjectName == null) {
      return null;
    }
    switch (subjectName) {
      case "Język polski" :
        return SubjectEnum.POLISH;
      case "Matematyka" :
        return SubjectEnum.MATH;
      case "Geografia" :
        return SubjectEnum.GEOGRAPHY;
      case "Historia" :
        return SubjectEnum.HISTORY;
      case "Nauki ścisłe" :
        return SubjectEnum.SCIENCE;
      case "Sztuka" :
        return SubjectEnum.ART;
      case "Wychowanie fizyczne" :
        return SubjectEnum.SPORTS;
      default :
        return null;
    }
  }

  @Named("translateSubject")
  default String translateSubject(SubjectEnum subject) {
    if (subject == null) {
      return "Nieznany przedmiot";
    }
    switch (subject) {
      case POLISH :
        return "Język polski";
      case MATH :
        return "Matematyka";
      case GEOGRAPHY :
        return "Geografia";
      case HISTORY :
        return "Historia";
      case SCIENCE :
        return "Nauki ścisłe";
      case ART :
        return "Sztuka";
      case SPORTS :
        return "Wychowanie fizyczne";
      default :
        return "Nieznany przedmiot";
    }
  }
}

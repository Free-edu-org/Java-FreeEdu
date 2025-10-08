package com.byt.freeEdu.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.Parent;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring", builder = @Builder(disableBuilder = true), nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper{

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "role", source = "userRole", qualifiedByName = "translateRole")
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "schoolClassId", ignore = true)
    @Mapping(target = "contactInfo", ignore = true)
    UserDto toDtoBase(User user);

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "role", source = "userRole", qualifiedByName = "translateRole")
    @Mapping(target = "parentId",
            expression = "java(student.getParent() != null ? student.getParent().getUserId() : 0)")
    @Mapping(target = "schoolClassId",
            expression = "java(student.getSchoolClass() != null ? student.getSchoolClass().getSchoolClassId() : 0)")
    @Mapping(target = "contactInfo", ignore = true)
    UserDto toDto(Student student);

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "role", source = "userRole", qualifiedByName = "translateRole")
    @Mapping(target = "parentId", constant = "0")
    @Mapping(target = "schoolClassId", constant = "0")
    @Mapping(target = "contactInfo", source = "contactInfo")
    UserDto toDto(Parent parent);

    default UserDto toDto(User user) {
        if (user instanceof Student s) return toDto(s);
        if (user instanceof Parent p)  return toDto(p);
        return toDtoBase(user);
    }

    // Listy
    default List<UserDto> toDtoList(List<? extends User> users) {
        if (users == null) return java.util.Collections.emptyList();
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

  @Named("translateRole")
  default String translateRole(String role) {
    switch (role) {
      case "TEACHER" :
        return "Nauczyciel";
      case "STUDENT" :
        return "Uczeń";
      case "ADMIN" :
        return "Administrator";
      case "PARENT" :
        return "Rodzic";
      default :
        return "Nieznana rola";
    }
  }
}

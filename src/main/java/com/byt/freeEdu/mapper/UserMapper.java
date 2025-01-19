package com.byt.freeEdu.mapper;

import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.User;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "role", source = "user_role", qualifiedByName = "translateRole")
    UserDto toDto(User user);

    @Named("translateRole")
    default String translateRole(String role) {
        switch (role) {
            case "TEACHER":
                return "Nauczyciel";
            case "STUDENT":
                return "Uczeń";
            case "ADMIN":
                return "Administrator";
            case "PARENT":
                return "Rodzic";
            default:
                return "Nieznana rola";
        }
    }
}

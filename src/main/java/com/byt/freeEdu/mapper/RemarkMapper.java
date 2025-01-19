package com.byt.freeEdu.mapper;

import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.Remark;
import org.mapstruct.Builder;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.format.DateTimeFormatter;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RemarkMapper {

    @Mapping(target = "remarkId", source = "remarkId")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "addDate", expression = "java(formatDate(remark.getAddDate()))")
    @Mapping(target = "studentName", expression = "java(remark.getStudent().getFirstname() + ' ' + remark.getStudent().getLastname())")
    RemarkDto toDto(Remark remark);

    default String formatDate(java.time.LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }
}

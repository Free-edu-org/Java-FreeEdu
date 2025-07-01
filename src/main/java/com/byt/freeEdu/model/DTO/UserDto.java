package com.byt.freeEdu.model.DTO;

import lombok.Data;

@Data
public class UserDto{

  private int id;

  private String firstname;

  private String lastname;

  private String username;

  private String email;

  private String role;

  private int parentId;

  private int schoolClassId;

  private String contactInfo;
}

package com.byt.freeEdu.controller;

import com.byt.freeEdu.config.AppConfig;
import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@WebFluxTest(controllers = ViewControllerAdmin.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AppConfig.class))
public class ViewControllerAdminTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private ScheduleService scheduleService;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setFirstname("Marek");
        userDto.setLastname("Kowalski");
        userDto.setEmail("Marekowalski@gmail.com");
        userDto.setRole("Nauczyciel");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testMainpageAdmin() {
        Mockito.when(sessionService.getUserId()).thenReturn(Mono.just(1));
        Mockito.when(userService.getUserById(1)).thenReturn(new User());
        Mockito.when(userMapper.toDto(Mockito.any(User.class))).thenReturn(userDto);

        webTestClient.get().uri("/view/admin/mainpage")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assert body != null;
                    assert body.contains("Marek");
                    assert body.contains("Kowalski");
                    assert body.contains("Marekowalski@gmail.com");
                    assert body.contains("Nauczyciel");
                });
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUserManagement() {
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(new User()));

        webTestClient.get().uri("/view/admin/user_management")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assert body != null;
                    assert body.contains("Zarządzanie użytkownikami");
                });
    }
}

package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(ADMIN_PATH + USERS_PATH)
public class UserAdminController {
    private final UserService userService;
    private final String path = ADMIN_PATH + USERS_PATH;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(required = false) Set<Long> ids,
            @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("Received GET {} request, ids: {}, from: {}. size: {}.", path, ids, from, size);
        return ResponseEntity.ok(userService.getUsersAdmin(ids, from, size));
    }

    @PostMapping
    public ResponseEntity<UserDto> saveNewUser(
            @Valid @RequestBody NewUserDto dto) {
        log.info("Received POST {} request, dto: {}.", path, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.saveNewUserAdmin(dto));
    }

    @DeleteMapping(USERS_ID_VAR)
    public ResponseEntity<Void> deleteUser(
            @PathVariable @Positive Long userId) {
        log.info("Received DELETE {}/{} request.", path, userId);
        userService.deleteUserAdmin(userId);
        return ResponseEntity.noContent().build();
    }
}

package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaginationAndEdgeCasesTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void paginationWithVariousParameters_shouldWorkCorrectly() {

        UserDto user = UserDto.builder()
                .name("Test User")
                .email("pagination@test.com")
                .build();
        UserDto savedUser = userService.create(user);
        Long userId = savedUser.getId();

        for (int i = 0; i < 5; i++) {
            ItemRequestDto request = new ItemRequestDto();
            request.setDescription("Request " + i);
            itemRequestService.create(request, userId);
        }

        assertDoesNotThrow(() -> itemRequestService.getOtherUsersRequests(userId, 0, 2));
        assertDoesNotThrow(() -> itemRequestService.getOtherUsersRequests(userId, 1, 3));
        assertDoesNotThrow(() -> itemRequestService.getOtherUsersRequests(userId, 0, 10));

        List<ItemRequestResponseDto> emptyPage = itemRequestService.getOtherUsersRequests(userId, 10, 5);
        assertNotNull(emptyPage);
    }

    @Test
    void emptyDatabaseOperations_shouldHandleGracefully() {

        UserDto tempUser = UserDto.builder()
                .name("Temp User")
                .email("temp@test.com")
                .build();
        UserDto savedTempUser = userService.create(tempUser);
        Long tempUserId = savedTempUser.getId();

        List<ItemRequestResponseDto> emptyRequests = itemRequestService.getByRequestor(tempUserId);
        assertNotNull(emptyRequests);
        assertTrue(emptyRequests.isEmpty());

        assertDoesNotThrow(() -> userService.delete(tempUserId));
        assertThrows(Exception.class, () -> userService.getById(tempUserId));
    }
}
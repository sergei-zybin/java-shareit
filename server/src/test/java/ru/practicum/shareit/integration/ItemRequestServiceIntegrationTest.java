package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("Requestor");
        requestor.setEmail("requestor@example.com");
        requestor = userRepository.save(requestor);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");
    }

    @Test
    void createItemRequest_shouldSaveRequestAndReturnDto() {
        ItemRequestResponseDto createdRequest = itemRequestService.create(itemRequestDto, requestor.getId());

        assertNotNull(createdRequest.getId());
        assertEquals(itemRequestDto.getDescription(), createdRequest.getDescription());
        assertNotNull(createdRequest.getCreated());
        assertTrue(createdRequest.getItems().isEmpty(), "Список вещей должен быть пустым для нового запроса.");

        ItemRequest savedRequest = itemRequestRepository.findById(createdRequest.getId()).orElse(null);
        assertNotNull(savedRequest);
        assertEquals(itemRequestDto.getDescription(), savedRequest.getDescription());
        assertEquals(requestor.getId(), savedRequest.getRequestor().getId());
        assertNotNull(savedRequest.getCreated());
    }
}
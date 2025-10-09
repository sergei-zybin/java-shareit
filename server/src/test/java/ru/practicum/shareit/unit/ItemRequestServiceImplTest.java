package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private ItemRequestDto itemRequestDto;
    private User requestor;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setId(1L);
        requestor.setName("Requestor");
        requestor.setEmail("requestor@example.com");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setRequest(itemRequest);
    }

    @Test
    void createItemRequest_withValidData_shouldReturnItemRequestDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestResponseDto result = itemRequestService.create(itemRequestDto, 1L);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertEquals(Collections.emptyList(), result.getItems());
    }

    @Test
    void createItemRequest_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(itemRequestDto, 1L));
    }

    @Test
    void getByRequestor_withValidUser_shouldReturnItemRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        List<ItemRequestResponseDto> result = itemRequestService.getByRequestor(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getByRequestor_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getByRequestor(999L));
    }

    @Test
    void getOtherUsersRequests_withValidData_shouldReturnPaginatedResults() {
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(eq(1L), any(Pageable.class)))
                .thenReturn(page);
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        List<ItemRequestResponseDto> result = itemRequestService.getOtherUsersRequests(1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getOtherUsersRequests_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getOtherUsersRequests(999L, 0, 10));
    }

    @Test
    void getById_withValidRequest_shouldReturnItemRequest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        ItemRequestResponseDto result = itemRequestService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getById_withNonExistentRequest_shouldThrowNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(999L, 1L));
    }

    @Test
    void getById_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L, 999L));
    }
}
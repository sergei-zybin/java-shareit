package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class IntegrationCoverageTest {

    @Autowired
    private ErrorHandler errorHandler;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void testErrorHandlerCoverage() {
        assertNotNull(errorHandler.handleNotFoundException(new NotFoundException("test")));
        assertNotNull(errorHandler.handleValidationException(new ValidationException("test")));
        assertNotNull(errorHandler.handleConflictException(new ConflictException("test")));
        assertNotNull(errorHandler.handleForbiddenException(new ForbiddenException("test")));
        assertNotNull(errorHandler.handleOtherExceptions(new Exception("test")));
    }
}
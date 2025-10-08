package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {"shareit-server.url=http://localhost:9090"})
class ShareItGatewayTest {

    @Autowired
    private ShareItGateway application;

    @Test
    void contextLoads() {
        assertNotNull(application);
    }
}
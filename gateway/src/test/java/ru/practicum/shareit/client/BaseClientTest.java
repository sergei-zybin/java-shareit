package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BaseClientTest {

    private BaseClient baseClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        baseClient = new BaseClient(restTemplate) {};
    }

    @Test
    void get_WithoutParameters_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test");

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void get_WithUserId_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test", 1L);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void get_WithUserIdAndParameters_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Map<String, Object> parameters = Map.of("key", "value");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test", 1L, parameters);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void post_WithBody_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Object requestBody = new Object();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.post("/test", requestBody);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void post_WithUserIdAndBody_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Object requestBody = new Object();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.post("/test", 1L, requestBody);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void post_WithUserIdParametersAndBody_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Object requestBody = new Object();
        Map<String, Object> parameters = Map.of("key", "value");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.post("/test", 1L, parameters, requestBody);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void patch_WithBody_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Object requestBody = new Object();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test", requestBody);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patch_WithUserId_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test", 1L);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patch_WithUserIdAndBody_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Object requestBody = new Object();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test", 1L, requestBody);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patch_WithUserIdParametersAndBody_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Object requestBody = new Object();
        Map<String, Object> parameters = Map.of("key", "value");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test", 1L, parameters, requestBody);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void put_WithUserIdAndBody_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Object requestBody = new Object();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.put("/test", 1L, requestBody);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void put_WithUserIdParametersAndBody_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Object requestBody = new Object();
        Map<String, Object> parameters = Map.of("key", "value");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.put("/test", 1L, parameters, requestBody);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void delete_WithoutParameters_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.delete("/test");

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void delete_WithUserId_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.delete("/test", 1L);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void delete_WithUserIdAndParameters_ShouldCallRestTemplate() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Map<String, Object> parameters = Map.of("key", "value");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.delete("/test", 1L, parameters);

        assertNotNull(response);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void makeAndSendRequest_WithHttpStatusCodeException_ShouldReturnErrorResponse() {
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(exception.getResponseBodyAsByteArray()).thenReturn("Error message".getBytes());

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> response = baseClient.get("/test");

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void defaultHeaders_WithUserId_ShouldSetHeaders() {
        BaseClient baseClient = new BaseClient(new RestTemplate()) {};

        HttpHeaders headers = baseClient.defaultHeaders(1L);

        assertNotNull(headers);
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(List.of(MediaType.APPLICATION_JSON), headers.getAccept());
        assertEquals("1", headers.getFirst("X-Sharer-User-Id"));
    }

    @Test
    void defaultHeaders_WithoutUserId_ShouldNotSetUserHeader() {
        BaseClient baseClient = new BaseClient(new RestTemplate()) {};

        HttpHeaders headers = baseClient.defaultHeaders(null);

        assertNotNull(headers);
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(List.of(MediaType.APPLICATION_JSON), headers.getAccept());
        assertNull(headers.getFirst("X-Sharer-User-Id"));
    }

    @Test
    void prepareGatewayResponse_With2xxResponse_ShouldReturnSameResponse() {
        ResponseEntity<Object> originalResponse = ResponseEntity.ok("Success");

        ResponseEntity<Object> result = BaseClient.prepareGatewayResponse(originalResponse);

        assertEquals(originalResponse, result);
    }

    @Test
    void prepareGatewayResponse_With4xxResponseAndBody_ShouldReturnErrorResponse() {
        ResponseEntity<Object> originalResponse = ResponseEntity.badRequest().body("Error");

        ResponseEntity<Object> result = BaseClient.prepareGatewayResponse(originalResponse);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Error", result.getBody());
    }

    @Test
    void prepareGatewayResponse_With4xxResponseWithoutBody_ShouldReturnErrorResponse() {
        ResponseEntity<Object> originalResponse = ResponseEntity.badRequest().build();

        ResponseEntity<Object> result = BaseClient.prepareGatewayResponse(originalResponse);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }
}
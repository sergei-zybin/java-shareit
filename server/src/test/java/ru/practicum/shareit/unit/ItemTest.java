package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testItemCreation() {

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);


        assertEquals(1L, item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void testItemWithOwnerAndRequest() {

        Item item = new Item();
        User owner = new User();
        owner.setId(10L);
        owner.setName("Owner");

        ItemRequest request = new ItemRequest();
        request.setId(20L);

        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);


        assertEquals(1L, item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testItemEqualsAndHashCode() {

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Item 2");

        Item item3 = new Item();
        item3.setId(2L);
        item3.setName("Item 1");

        assertEquals(item1, item2); // Same ID
        assertNotEquals(item1, item3); // Different ID
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1.hashCode(), item3.hashCode());
    }

    @Test
    void testItemEqualsWithNull() {

        Item item = new Item();
        item.setId(1L);

        assertNotEquals(null, item);
        assertNotEquals(item, new Object());
    }

    @Test
    void testItemToString() {

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        String toString = item.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test Item"));
        assertTrue(toString.contains("description=Test Description"));
        assertTrue(toString.contains("available=true"));
    }

    @Test
    void testItemAvailability() {

        Item availableItem = new Item();
        availableItem.setAvailable(true);

        Item unavailableItem = new Item();
        unavailableItem.setAvailable(false);

        assertTrue(availableItem.getAvailable());
        assertFalse(unavailableItem.getAvailable());
    }
}
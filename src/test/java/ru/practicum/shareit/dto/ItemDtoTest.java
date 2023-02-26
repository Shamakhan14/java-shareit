package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    public void testCommentDtoInc() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    public void testBookingDtoCreateDeserialization() throws Exception {
        String object = "{\"id\":\"1\",\"name\":\"name\",\"description\":\"description\",\"available\":\"true\",\"requestId\":\"1\"}";

        ItemDto itemDto = json.parseObject(object);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("name");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getRequestId()).isEqualTo(1L);
    }
}

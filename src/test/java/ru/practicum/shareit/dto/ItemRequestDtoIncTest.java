package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDtoInc;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoIncTest {

    @Autowired
    private JacksonTester<ItemRequestDtoInc> json;

    @Test
    public void testCommentDtoInc() throws Exception {
        ItemRequestDtoInc itemRequestDtoInc = new ItemRequestDtoInc("text");

        JsonContent<ItemRequestDtoInc> result = json.write(itemRequestDtoInc);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("text");
    }

    @Test
    public void testBookingDtoCreateDeserialization() throws Exception {
        String object = "{\"description\":\"text\"}";

        ItemRequestDtoInc itemRequestDtoInc = json.parseObject(object);

        assertThat(itemRequestDtoInc.getDescription()).isEqualTo("text");
    }
}

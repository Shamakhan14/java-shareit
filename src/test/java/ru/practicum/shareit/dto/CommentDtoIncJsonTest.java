package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDtoInc;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoIncJsonTest {

    @Autowired
    private JacksonTester<CommentDtoInc> json;

    @Test
    public void testCommentDtoInc() throws Exception {
        CommentDtoInc commentDtoInc = new CommentDtoInc("text");

        JsonContent<CommentDtoInc> result = json.write(commentDtoInc);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    public void testBookingDtoCreateDeserialization() throws Exception {
        String object = "{\"text\":\"text\"}";

        CommentDtoInc commentDtoInc = json.parseObject(object);

        assertThat(commentDtoInc.getText()).isEqualTo("text");
    }
}

package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoCreateJsonTest {

    @Autowired
    private JacksonTester<BookingDtoCreate> json;

    @Test
    public void testBookingDtoCreate() throws Exception {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                1L,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 1, 1, 1, 1)
        );

        JsonContent<BookingDtoCreate> result = json.write(bookingDtoCreate);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-01T01:01:01");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-01T01:01:01");
    }

    @Test
    public void testBookingDtoCreateDeserialization() throws Exception {
        String object = "{\"itemId\":\"1\",\"start\":\"2023-01-01T01:01:01\",\"end\":\"2024-01-01T01:01:01\"}";

        BookingDtoCreate dtoCreate = json.parseObject(object);

        assertThat(dtoCreate.getItemId()).isEqualTo(1L);
        assertThat(dtoCreate.getStart()).isEqualTo(LocalDateTime.of(2023,1,1,1,1,1));
        assertThat(dtoCreate.getEnd()).isEqualTo(LocalDateTime.of(2024,1,1,1,1,1));
    }
}

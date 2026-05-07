package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toDto(Item item) {
        Long requestId = item.getRequest() == null ? null : item.getRequest().getId();
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId,
                null,
                null,
                List.of()
        );
    }

    public static ItemDto toDto(Item item,
                                Booking lastBooking,
                                Booking nextBooking,
                                List<Comment> comments) {
        ItemDto dto = toDto(item);
        dto.setLastBooking(toShortDto(lastBooking));
        dto.setNextBooking(toShortDto(nextBooking));
        dto.setComments(comments.stream().map(ItemMapper::toCommentDto).toList());
        return dto;
    }

    public static Item toModel(ItemDto itemDto, User owner, ItemRequest request) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                request
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    private static BookingShortDto toShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingShortDto(booking.getId(), booking.getBooker().getId());
    }
}

package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public synchronized Item save(Item item) {
        Long id = idGenerator.getAndIncrement();
        Item savedItem = new Item(
                id,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId()
        );
        items.put(id, savedItem);
        return copy(savedItem);
    }

    @Override
    public synchronized Item update(Item item) {
        Item updatedItem = copy(item);
        items.put(updatedItem.getId(), updatedItem);
        return copy(updatedItem);
    }

    @Override
    public synchronized Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId)).map(this::copy);
    }

    @Override
    public synchronized List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(this::copy)
                .toList();
    }

    @Override
    public synchronized List<Item> searchAvailableByText(String text) {
        String lowerCaseText = text.toLowerCase(Locale.ROOT);
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> containsIgnoreCase(item.getName(), lowerCaseText)
                        || containsIgnoreCase(item.getDescription(), lowerCaseText))
                .map(this::copy)
                .toList();
    }

    private boolean containsIgnoreCase(String value, String lowerCaseText) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(lowerCaseText);
    }

    private Item copy(Item item) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId()
        );
    }
}

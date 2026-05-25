package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner_IdOrderByIdAsc(Long ownerId);

    List<Item> findByRequest_IdOrderByIdAsc(Long requestId);

    List<Item> findByRequest_IdInOrderByIdAsc(Collection<Long> requestIds);

    @Query("select i from Item i "
            + "where i.available = true "
            + "and (lower(i.name) like lower(concat('%', :text, '%')) "
            + "or lower(i.description) like lower(concat('%', :text, '%'))) "
            + "order by i.id")
    List<Item> searchAvailableByText(@Param("text") String text);
}

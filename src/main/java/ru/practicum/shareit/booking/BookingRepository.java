package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(Long bookerId,
                                                            LocalDateTime start,
                                                            LocalDateTime end,
                                                            Pageable pageable);

    List<Booking> findByBooker_IdAndEndBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndStartAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItem_Owner_Id(Long ownerId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId,
                                                                LocalDateTime start,
                                                                LocalDateTime end,
                                                                Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndBefore(Long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartAfter(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndStatusAndEndBeforeOrderByEndDesc(Long itemId,
                                                                            BookingStatus status,
                                                                            LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId,
                                                                               BookingStatus status,
                                                                               LocalDateTime start);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(Long itemId,
                                                              Long bookerId,
                                                              BookingStatus status,
                                                              LocalDateTime end);
}

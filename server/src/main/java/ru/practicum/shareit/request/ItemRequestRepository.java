package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    ItemRequest save(ItemRequest itemRequest);

    List<ItemRequest> findByRequestorId(long requestorId);

    List<ItemRequest> findByRequestorIdNot(long requestorId);

    Optional<ItemRequest> findById(long id);
}

package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestResponse;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(long ownerId);

    Optional<Item> findById(long id);

    @Query("select i from Item i "
            + "where (upper(i.name) like upper(concat('%', ?1, '%')) "
            + "or upper(i.description) like upper(concat('%', ?1, '%'))) "
            + "and available = true")
    List<Item> findByNameOrDescription(String text);

    Item save(Item item);

    @Query("select new ru.practicum.shareit.request.ItemRequestResponse(i.id, i.name, i.owner.id, i.requestId) from Item i " +
            "where i.requestId = ?1")
    List<ItemRequestResponse> findByRequestId(long requestId);

    @Query("select new ru.practicum.shareit.request.ItemRequestResponse(i.id, i.name, i.owner.id, i.requestId) " +
            "from Item i " +
            "where i.requestId in ?1")
    List<ItemRequestResponse> findByRequestIdIn(List<Long> requestIds);

}

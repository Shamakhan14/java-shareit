package ru.practicum.shareit.request;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(Long userId);

    @Query(value = "select i from ItemRequest i " +
            "where i.requestor.id <> ?1 " +
            "group by i.id")
    List<ItemRequest> findAllWithoutUser(Long userId, Sort sort);

    @Query(value = "select i from ItemRequest i " +
            "where i.requestor.id <> ?1 " +
            "group by i.id")
    List<ItemRequest> findAllPageable(Long userId, Pageable pageable);
}

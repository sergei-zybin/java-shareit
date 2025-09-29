package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    List<Comment> findByItemIdIn(List<Long> itemIds);

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.item.id = :itemId")
    List<Comment> findByItemIdWithAuthor(Long itemId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.item.id IN :itemIds")
    List<Comment> findByItemIdInWithAuthors(List<Long> itemIds);
}
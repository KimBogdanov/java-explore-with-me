package ru.practicum.mainmodule.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainmodule.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByIdIn(List<Integer> ids, Pageable pageable);

    Page<User> findAll(Pageable pageable);
}

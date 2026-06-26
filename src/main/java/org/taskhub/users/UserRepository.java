package org.taskhub.users;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByMail(String email);
    Optional<User> findByMail(String email);

}

package ma.kaoutar.userservice.dao.repositories;

import ma.kaoutar.userservice.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

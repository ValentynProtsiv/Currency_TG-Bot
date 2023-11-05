package bot.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByChatId(Long chatId);
    User findByChatId(Long chatId);

}
package bot.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository repository;

    public void save(User user){
        repository.save(user);
    }
    public boolean existByChatId(Long chatId){
        return repository.existsByChatId(chatId);
    }
    public User findChatId (Long chatId){
        return repository.findByChatId(chatId);
    }

}

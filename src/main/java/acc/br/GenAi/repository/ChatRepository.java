package acc.br.GenAi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import acc.br.GenAi.model.Chat;

@Repository
public interface ChatRepository extends CrudRepository<Chat, Long> {

}

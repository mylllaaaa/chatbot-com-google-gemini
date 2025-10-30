package acc.br.GenAi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acc.br.GenAi.model.Chat;
import acc.br.GenAi.repository.ChatRepository;

@Service
public class ChatService {
	
	@Autowired
	GoogleGenAiChatModel geminiModel;
	
	@Autowired
	ChatRepository rep; 
	
	public List<Chat> findAllChats(){
		List<Chat> lista = new ArrayList<Chat>();
		rep.findAll().forEach(c -> lista.add(c));
		return lista;
	}
	
	public Chat savingQuestion(String userQuestion) {
		String aiAnswer = geminiModel.call(userQuestion);
		Chat chatInteraction = new Chat(userQuestion, aiAnswer);
		rep.save(chatInteraction);
		return chatInteraction;
	}
	
	public void deleteChat(Long id) {
		rep.deleteById(id);
	}
}

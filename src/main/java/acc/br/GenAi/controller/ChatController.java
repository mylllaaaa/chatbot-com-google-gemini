package acc.br.GenAi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import acc.br.GenAi.model.Chat;
import acc.br.GenAi.service.ChatService;


@RestController
@RequestMapping("/question")
public class ChatController {
	
	@Autowired
	ChatService service;

	@PostMapping("/chat")
	public ResponseEntity<Chat> askingQuestion(@RequestBody String userMessage) {
		Chat aux = service.savingQuestion(userMessage);
		return ResponseEntity.ok().body(aux);
	}
	
	@GetMapping("/allChats")
	public ResponseEntity<List<Chat>> findAllChats() {
		List<Chat> lista = service.findAllChats();
		return ResponseEntity.ok().body(lista);
	}
	
	@DeleteMapping("/chat/{id}")
	public ResponseEntity<Void> deleteChat(@PathVariable Long id){
		service.deleteChat(id);
		return ResponseEntity.noContent().build();
	}
	
}

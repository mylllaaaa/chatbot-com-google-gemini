package joptionpane;

import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import acc.br.GenAi.model.Chat;

public class ChatMain {

	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		String baseUrl = "http://localhost:8080/question";

		while (true) {
			// Menu principal
			String[] options = { "Ask a question (POST)", "List all chats (GET)", "Delete Chat (DELETE)", "Edit question (PUT)", "Leave" };
			int choice = JOptionPane.showOptionDialog(null, "Escolha uma operação:", "Cliente ChatBot",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

			if (choice == 4 || choice == JOptionPane.CLOSED_OPTION) {
				JOptionPane.showMessageDialog(null, "Encerrando...");
				break;
			}

			try {
				switch (choice) {
				case 0: // POST
					String userMessage = JOptionPane.showInputDialog("Digite sua pergunta:");
					if (userMessage == null || userMessage.trim().isEmpty())
						break;

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.TEXT_PLAIN);
					HttpEntity<String> request = new HttpEntity<>(userMessage, headers);

					ResponseEntity<Chat> postResponse = restTemplate.postForEntity(baseUrl + "/chat", request,
							Chat.class);

					JOptionPane.showMessageDialog(null, "🤖 Resposta:\n" + postResponse.getBody().getAiAnswer());
					break;

				case 1: // GET
					ResponseEntity<Chat[]> getResponse = restTemplate.getForEntity(baseUrl + "/allChats", Chat[].class);

					List<Chat> chats = Arrays.asList(getResponse.getBody());
					StringBuilder sb = new StringBuilder("📜 Histórico:\n");
					for (Chat c : chats) {
						sb.append("ID: ").append(c.getId()).append("\n").append("Pergunta: ")
								.append(c.getUserQuestion()).append("\n").append("Resposta: ").append(c.getAiAnswer())
								.append("\n\n");
					}

					JOptionPane.showMessageDialog(null, sb.toString());
					break;

				case 2: // DELETE
					String idStr = JOptionPane.showInputDialog("Digite o ID da conversa para excluir:");
					if (idStr == null || idStr.trim().isEmpty())
						break;

					Long id = Long.parseLong(idStr);
					restTemplate.delete(baseUrl + "/chat/" + id);

					JOptionPane.showMessageDialog(null, "Conversa ID " + id + " excluída com sucesso!");
					break;
				case 3:
					Long id2 = Long.parseLong(JOptionPane.showInputDialog("Digite o ID da pergunta que deseja editar:"));
					String newQuestion = JOptionPane.showInputDialog("Digite a nova pergunta:");

					String urlPut = "http://localhost:8080/question/edit/" + id2;

					RestTemplate restTemplate2 = new RestTemplate();
					HttpHeaders headers2 = new HttpHeaders();
					headers2.setContentType(MediaType.APPLICATION_JSON);

					HttpEntity<String> request2 = new HttpEntity<>(newQuestion, headers2);

					ResponseEntity<Chat> responsePut = restTemplate2.exchange(urlPut, HttpMethod.PUT, request2,
							Chat.class);

					Chat editedChat = responsePut.getBody();
					JOptionPane.showMessageDialog(null,	"Pergunta atualizada com sucesso!\n\nNova resposta: " + editedChat.getAiAnswer());
					break;
				}

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}

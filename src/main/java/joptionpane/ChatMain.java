package joptionpane;

import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
            String[] options = {"Fazer pergunta (POST)", "Listar conversas (GET)", "Excluir conversa (DELETE)", "Sair"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Escolha uma operação:",
                    "Cliente ChatBot",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(null, "Encerrando...");
                break;
            }

            try {
                switch (choice) {
                    case 0: // POST
                        String userMessage = JOptionPane.showInputDialog("Digite sua pergunta:");
                        if (userMessage == null || userMessage.trim().isEmpty()) break;

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.TEXT_PLAIN);
                        HttpEntity<String> request = new HttpEntity<>(userMessage, headers);

                        ResponseEntity<Chat> postResponse = restTemplate.postForEntity(
                                baseUrl + "/chat", request, Chat.class);

                        JOptionPane.showMessageDialog(null,
                                "🤖 Resposta:\n" + postResponse.getBody().getAiAnswer());
                        break;

                    case 1: // GET
                        ResponseEntity<Chat[]> getResponse = restTemplate.getForEntity(
                                baseUrl + "/allChats", Chat[].class);

                        List<Chat> chats = Arrays.asList(getResponse.getBody());
                        StringBuilder sb = new StringBuilder("📜 Histórico:\n\n");
                        for (Chat c : chats) {
                            sb.append("ID: ").append(c.getId()).append("\n")
                              .append("Pergunta: ").append(c.getUserQuestion()).append("\n")
                              .append("Resposta: ").append(c.getAiAnswer()).append("\n\n");
                        }

                        JOptionPane.showMessageDialog(null, sb.toString());
                        break;

                    case 2: // DELETE
                        String idStr = JOptionPane.showInputDialog("Digite o ID da conversa para excluir:");
                        if (idStr == null || idStr.trim().isEmpty()) break;

                        Long id = Long.parseLong(idStr);
                        restTemplate.delete(baseUrl + "/chat/" + id);

                        JOptionPane.showMessageDialog(null, "Conversa ID " + id + " excluída com sucesso!");
                        break;
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}


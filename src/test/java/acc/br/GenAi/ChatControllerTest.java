package acc.br.GenAi;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import acc.br.GenAi.controller.ChatController;
import acc.br.GenAi.model.Chat;
import acc.br.GenAi.service.ChatService;

class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private Chat chat1;
    private Chat chat2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();

        chat1 = new Chat("Hello", "Hi there!");
        chat1.setId(1L);

        chat2 = new Chat("How are you?", "I'm fine!");
        chat2.setId(2L);
    }

    @Test
    void testAskingQuestion() throws Exception {
        String userMessage = "What is AI?";
        Chat chatResponse = new Chat(userMessage, "AI stands for Artificial Intelligence.");
        when(chatService.savingQuestion(userMessage)).thenReturn(chatResponse);

        mockMvc.perform(post("/question/chat")
                .contentType(MediaType.TEXT_PLAIN_VALUE) // texto simples, não JSON
                .content(userMessage))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userQuestion").value(userMessage))
                .andExpect(jsonPath("$.aiAnswer").value("AI stands for Artificial Intelligence."));

        verify(chatService, times(1)).savingQuestion(userMessage);
    }

    @Test
    void testFindAllChats() throws Exception {
        List<Chat> chats = Arrays.asList(chat1, chat2);
        when(chatService.findAllChats()).thenReturn(chats);

        mockMvc.perform(get("/question/allChats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userQuestion").value("Hello"))
                .andExpect(jsonPath("$[1].userQuestion").value("How are you?"));

        verify(chatService, times(1)).findAllChats();
    }

    @Test
    void testDeleteChat() throws Exception {
        doNothing().when(chatService).deleteChat(1L);

        mockMvc.perform(delete("/question/chat/1"))
                .andExpect(status().isNoContent());

        verify(chatService, times(1)).deleteChat(1L);
    }

    @Test
    void testPutMethodName() throws Exception {
        String newQuestion = "What is Java?";
        Chat updated = new Chat(newQuestion, "Java is a programming language.");
        updated.setId(1L);

        when(chatService.updateUserQuestion(1L, newQuestion)).thenReturn(updated);

        mockMvc.perform(put("/question/edit/1")
                .contentType(MediaType.TEXT_PLAIN_VALUE) // texto simples
                .content(newQuestion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userQuestion").value(newQuestion))
                .andExpect(jsonPath("$.aiAnswer").value("Java is a programming language."));

        verify(chatService, times(1)).updateUserQuestion(1L, newQuestion);
    }
}

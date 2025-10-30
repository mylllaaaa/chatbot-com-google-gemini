package acc.br.GenAi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;

import acc.br.GenAi.model.Chat;
import acc.br.GenAi.repository.ChatRepository;
import acc.br.GenAi.service.ChatService;

class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private GoogleGenAiChatModel geminiModel;

    @InjectMocks
    private ChatService chatService;

    private Chat chat1;
    private Chat chat2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        chat1 = new Chat("Hello", "Hi there!");
        chat1.setId(1L);

        chat2 = new Chat("How are you?", "I'm fine!");
        chat2.setId(2L);
    }

    @Test
    void testFindAllChats() {
        when(chatRepository.findAll()).thenReturn(Arrays.asList(chat1, chat2));

        List<Chat> chats = chatService.findAllChats();

        assertEquals(2, chats.size());
        verify(chatRepository, times(1)).findAll();
    }

    @Test
    void testGetChatById() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat1));

        Chat result = chatService.getChatById(1L);

        assertEquals("Hello", result.getUserQuestion());
        verify(chatRepository, times(1)).findById(1L);
    }

    @Test
    void testSavingQuestion() {
        String question = "What is AI?";
        String expectedAnswer = "AI stands for Artificial Intelligence.";
        when(geminiModel.call(question)).thenReturn(expectedAnswer);
        when(chatRepository.save(any(Chat.class))).thenAnswer(i -> i.getArgument(0));

        Chat result = chatService.savingQuestion(question);

        assertEquals(question, result.getUserQuestion());
        assertEquals(expectedAnswer, result.getAiAnswer());
        verify(chatRepository, times(1)).save(any(Chat.class));
        verify(geminiModel, times(1)).call(question);
    }

    @Test
    void testDeleteChat() {
        doNothing().when(chatRepository).deleteById(1L);

        chatService.deleteChat(1L);

        verify(chatRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateUserQuestion() {
        String newQuestion = "What is Java?";
        String newAnswer = "Java is a programming language.";
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat1));
        when(geminiModel.call(newQuestion)).thenReturn(newAnswer);
        when(chatRepository.save(any(Chat.class))).thenAnswer(i -> i.getArgument(0));

        Chat updated = chatService.updateUserQuestion(1L, newQuestion);

        assertEquals(newQuestion, updated.getUserQuestion());
        assertEquals(newAnswer, updated.getAiAnswer());
        verify(chatRepository, times(1)).save(chat1);
    }

    @Test
    void testUpdateUserQuestion_ChatNotFound() {
        when(chatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chatService.updateUserQuestion(99L, "Test"));
    }
}

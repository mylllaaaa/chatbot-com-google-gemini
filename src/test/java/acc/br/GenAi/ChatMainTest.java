package acc.br.GenAi;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import acc.br.GenAi.model.Chat;
import joptionpane.ChatMain;

public class ChatMainTest {

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAskQuestion_POST() throws Exception {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showOptionDialog(
                    any(), any(), any(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(0)
                    .thenReturn(4);

            mockedPane.when(() -> JOptionPane.showInputDialog(anyString())).thenReturn("Qual a capital da França?");
            mockedPane.when(() -> JOptionPane.showMessageDialog(any(), anyString())).thenAnswer(i -> null);

            // mocka resposta do servidor
            Chat mockChat = new Chat("Qual a capital da França?", "Paris");
            ResponseEntity<Chat> mockResponse = new ResponseEntity<>(mockChat, HttpStatus.OK);

            try (MockedConstruction<RestTemplate> mockRest = mockConstruction(RestTemplate.class,
                    (mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Chat.class))).thenReturn(mockResponse))) {

                ChatMain.main(new String[]{});
                RestTemplate rest = mockRest.constructed().get(0);
                verify(rest, times(1)).postForEntity(contains("/chat"), any(), eq(Chat.class));
            }
        }
    }

    @Test
    void testListChats_GET() throws Exception {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showOptionDialog(
                    any(), any(), any(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(1) // GET
                    .thenReturn(4); // sair

            mockedPane.when(() -> JOptionPane.showMessageDialog(any(), anyString())).thenAnswer(i -> null);

            Chat[] mockChats = {
                    new Chat("Pergunta 1", "Resposta 1"),
                    new Chat("Pergunta 2", "Resposta 2")
            };
            ResponseEntity<Chat[]> mockResponse = new ResponseEntity<>(mockChats, HttpStatus.OK);

            try (MockedConstruction<RestTemplate> mockRest = mockConstruction(RestTemplate.class,
                    (mock, ctx) -> when(mock.getForEntity(anyString(), eq(Chat[].class))).thenReturn(mockResponse))) {

                ChatMain.main(new String[]{});
                RestTemplate rest = mockRest.constructed().get(0);
                verify(rest, times(1)).getForEntity(contains("/allChats"), eq(Chat[].class));
            }
        }
    }

    @Test
    void testDeleteChat_DELETE() throws Exception {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showOptionDialog(
                    any(), any(), any(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(2) // DELETE
                    .thenReturn(4); // sair

            mockedPane.when(() -> JOptionPane.showInputDialog(anyString())).thenReturn("1");
            mockedPane.when(() -> JOptionPane.showMessageDialog(any(), anyString())).thenAnswer(i -> null);

            try (MockedConstruction<RestTemplate> mockRest = mockConstruction(RestTemplate.class,
                    (mock, ctx) -> doNothing().when(mock).delete(anyString()))) {

                ChatMain.main(new String[]{});
                RestTemplate rest = mockRest.constructed().get(0);
                verify(rest, times(1)).delete(contains("/chat/1"));
            }
        }
    }

    @Test
    void testEditQuestion_PUT() throws Exception {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showOptionDialog(
                    any(), any(), any(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(3) 
                    .thenReturn(4); 

            mockedPane.when(() -> JOptionPane.showInputDialog(anyString()))
                    .thenReturn("1") // ID
                    .thenReturn("Qual é a capital da Alemanha?");

            mockedPane.when(() -> JOptionPane.showMessageDialog(any(), anyString())).thenAnswer(i -> null);

            Chat mockChat = new Chat("Qual é a capital da Alemanha?", "Berlim");
            ResponseEntity<Chat> mockResponse = new ResponseEntity<>(mockChat, HttpStatus.OK);

            try (MockedConstruction<RestTemplate> mockRest = mockConstruction(RestTemplate.class,
                    (mock, ctx) -> when(mock.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(Chat.class)))
                            .thenReturn(mockResponse))) {

                ChatMain.main(new String[]{});
                RestTemplate rest = mockRest.constructed().get(0);
                verify(rest, times(1)).exchange(contains("/edit/1"), eq(HttpMethod.PUT), any(), eq(Chat.class));
            }
        }
    }

    @Test
    void testExitOption() {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showOptionDialog(any(), any(), any(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(4); 
            mockedPane.when(() -> JOptionPane.showMessageDialog(any(), anyString())).thenAnswer(i -> null);
            try {
                ChatMain.main(new String[]{});
            } catch (Exception e) {
                fail("O método não deveria lançar exceção: " + e.getMessage());
            }

        }
    }
}

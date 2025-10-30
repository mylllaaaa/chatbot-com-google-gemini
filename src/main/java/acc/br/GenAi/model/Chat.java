package acc.br.GenAi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Chat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String userQuestion;
	@Lob
	private String aiAnswer;
	
	public Chat() {
		
	}

	public Chat(String userQuestion, String aiAnswer) {
		super();
		this.userQuestion = userQuestion;
		this.aiAnswer = aiAnswer;
	}

	public Long getId() {
		return id;
	}

	public String getUserQuestion() {
		return userQuestion;
	}

	public void setUserQuestion(String userQuestion) {
		this.userQuestion = userQuestion;
	}

	public String getAiAnswer() {
		return aiAnswer;
	}

	public void setAiAnswer(String aiAnswer) {
		this.aiAnswer = aiAnswer;
	}

	@Override
	public String toString() {
		return "Chat [userQuestion=" + userQuestion + ", aiAnswer=" + aiAnswer + "]";
	}
	
}

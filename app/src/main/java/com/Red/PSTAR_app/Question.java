package com.Red.PSTAR_app;

public class Question {
	public static final int ExamCount = 50;

	public String Qno;
	public String Question;
	public String A;
	public String B;
	public String C;
	public String D;
	public String Correct;
	public String Category;

	public static String getCorrectByLetter(Question question, String letter) {
		switch (letter) {
			case "A": return question.A;
			case "B": return question.B;
			case "C": return question.C;
			case "D": return question.D;
			default: throw new RuntimeException("Unknown letter");
		}
	}
}
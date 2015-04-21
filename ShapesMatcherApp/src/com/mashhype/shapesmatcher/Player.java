package com.mashhype.shapesmatcher;

public class Player {

	int score = 0;
	
	public Player() {
		
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void updateScore(int newScore) {
		this.score = score + newScore; 
	}
}

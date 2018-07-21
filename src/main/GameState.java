package main;

import java.util.ArrayList;

public class GameState {
	
	private int currentState;
	ArrayList<Integer> states = new ArrayList<Integer>();
	
	public GameState(int newState) {
		currentState = newState;
	}
	
	public void setState(int newState) {
		currentState = newState;
	}
	
	public int getState() {
		return currentState;
	}
	
	
}

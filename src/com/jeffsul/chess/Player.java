package com.jeffsul.chess;

import com.jeffsul.chess.pieces.King;
import com.jeffsul.chess.pieces.Rook;

public class Player {
	public enum Colour {WHITE, BLACK};
	
	private King king;
	private Rook kingRook;
	private Rook queenRook;
	private Colour colour;
	
	public Player(Colour c) {
		colour = c;
	}
	
	public King getKing() {
		return king;
	}
	
	public void setKing(King king) {
		this.king = king;
	}
	
	public Rook getKingRook() {
		return kingRook;
	}
	
	public Rook getQueenRook() {
		return queenRook;
	}
	
	public void setKingRook(Rook rook) {
		kingRook = rook;
	}
	
	public void setQueenRook(Rook rook) {
		queenRook = rook;
	}
	
	public Colour getColour() {
		return colour;
	}
	
	public String toString() {
		if (colour == Colour.WHITE)
			return "White";
		return "Black";
	}
}

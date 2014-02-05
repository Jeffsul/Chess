package com.jeffsul.chess.pieces;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.jeffsul.chess.Board;
import com.jeffsul.chess.Player;

public abstract class Piece {
	private String pieceChar;
	private int x;
	private int y;
	protected final Player player;
	
	protected Board board;
	private ImageIcon img;
	
	private boolean hasMoved;
	
	public Piece(Player player, Board b, char pc, String name) {
		this.player = player;
		board = b;
		pieceChar = Character.toString(pc);
		
		String mod = (player.getColour() == Player.Colour.WHITE) ? "white" : "black";
		img = new ImageIcon(getClass().getResource(mod + name + ".png"));
	}
	
	@Override
	public String toString() {
		return pieceChar;
	}
	
	public boolean canAttack(Piece piece) {
		return canAttack(piece.x, piece.y);
	}
	
	public boolean canAttack(int x, int y) {
		return getSquares(true).contains(new Point(x, y));
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ImageIcon getImage() {
		return img;
	}
	
	public abstract ArrayList<Point> getSquares(boolean check);
}
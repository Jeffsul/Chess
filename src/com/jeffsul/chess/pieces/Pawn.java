package com.jeffsul.chess.pieces;

import java.awt.Point;
import java.util.ArrayList;

import com.jeffsul.chess.Board;
import com.jeffsul.chess.Player;

public class Pawn extends Piece {
	
	private boolean doubleJumped;
	
	public Pawn(Player p, Board c) {
		super(p, c, 'P', "Pawn");
	}
	
	public boolean canAttack(Point sq) {
		int y = (player == board.white) ? getY() + 1 : getY() - 1;
		if ((sq.x == getX() + 1 || sq.x == getX() - 1) && sq.y == y)
			return true;
		return false;
	}
	
	public ArrayList<Point> getSquares(boolean check) {
		ArrayList<Point> sq = new ArrayList<Point>();
		int x = getX();
		int y = (player == board.white) ? getY() + 1 : getY() - 1;
		int dy = y - this.y;
		if (board.isClear(x, y, player) && !board.isOpponent(x, y, player)) {	
			if (check || !board.isInCheck(x, y, player, this))
				sq.add(new Point(x, y));	
			if (!hasMoved() && board.isClear(x, y + dy, player) && !board.isOpponent(x, y + dy, player) && (check || !board.isInCheck(x, y + dy, player, this)))
				sq.add(new Point(x, y + dy));
		}
		
		x = this.x + 1;
		if (x <= 7 && board.isOpponent(x, y, player) && (check || !board.isInCheck(x, y, player, this)))
			sq.add(new Point(x, y));
		x = this.x - 1;
		if (x >= 0 && board.isOpponent(x, y, player) && (check || !board.isInCheck(x, y, player, this)))
			sq.add(new Point(x, y));
		return sq;
	}
	
	public void setDoubleJumped(boolean hasDoubleJumped) {
		doubleJumped = hasDoubleJumped;
	}
	
	public boolean hasDoubleJumped() {
		return doubleJumped;
	}
}

package com.jeffsul.chess.pieces;

import java.awt.Point;
import java.util.ArrayList;

import com.jeffsul.chess.Board;
import com.jeffsul.chess.Player;

public class Bishop extends Piece {
	private static final int[][] MOVE_DIRECTIONS = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
	
	public Bishop(Player p, Board c) {
		super(p, c, 'B', "Bishop");
	}

	@Override
	public ArrayList<Point> getSquares(boolean check) {
		ArrayList<Point> sq = new ArrayList<Point>();
		for (int[] pair : MOVE_DIRECTIONS) {
			int dx = pair[0];
			int dy = pair[1];
			int newX = getX() + dx;
			int newY = getY() + dy;
			while (newX <= 7 && newX >= 0 && newY <= 7 && newY >= 0 && board.isClear(newX, newY, player)) {
				if (check || !board.isInCheck(newX, newY, player, this))
					sq.add(new Point(newX, newY));
				
				if (board.isOpponent(newX, newY, player))
					break;
				
				newX += dx;
				newY += dy;
			}
		}
		return sq;
	}
}

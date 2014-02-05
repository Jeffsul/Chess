package com.jeffsul.chess.pieces;

import java.awt.Point;
import java.util.ArrayList;

import com.jeffsul.chess.Board;
import com.jeffsul.chess.Player;

public class Knight extends Piece {
	private static final int[][] MOVE_DIRECTIONS = {{-1, 2}, {1, 2}, {1, -2}, {-1, -2}, {-2, 1}, {-2, -1}, {2, -1}, {2, 1}};
	
	public Knight(Player p, Board c) {
		super(p, c, 'N', "Knight");
	}

	@Override
	public ArrayList<Point> getSquares(boolean check) {
		ArrayList<Point> sq = new ArrayList<Point>();
		for (int[] pair : MOVE_DIRECTIONS) {
			int newX = getX() + pair[0];
			int newY = getY() + pair[1];
			if (newX <= 7 && newX >= 0 && newY <= 7 && newY >= 0 && board.isClear(newX, newY, player)) {
				if (check || !board.isInCheck(newX, newY, player, this))
					sq.add(new Point(newX, newY));
			}
		}
		return sq;
	}
}

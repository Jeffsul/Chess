package com.jeffsul.chess;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.jeffsul.chess.pieces.Bishop;
import com.jeffsul.chess.pieces.King;
import com.jeffsul.chess.pieces.Knight;
import com.jeffsul.chess.pieces.Pawn;
import com.jeffsul.chess.pieces.Piece;
import com.jeffsul.chess.pieces.Queen;
import com.jeffsul.chess.pieces.Rook;

public class Board {
	
	private Piece[][] board;
	private Player white;
	private Player black;
	
	public Board(Player white, Player black) {
		this.white = white;
		this.black = black;

		for (int i = 0; i < 8; i++) {
			setPiece(i, 1, new Pawn(white, this));
			setPiece(i, 6, new Pawn(black, this));
		}
		
		for (int i = 0; i < 2; i++) {
			Player player = (i == 0) ? white : black;
			int y = i * 7;
			setPiece(1, y, new Knight(player, this));
			setPiece(6, y, new Knight(player, this));
			setPiece(5, y, new Bishop(player, this));
			setPiece(2, y, new Bishop(player, this));
			setPiece(3, y, new Queen(player, this));
			player.setKing(new King(player, this));
			player.setKingRook(new Rook(player, this));
			player.setQueenRook(new Rook(player, this));
			setPiece(0, y, player.getQueenRook());
			setPiece(7, y, player.getKingRook());
			setPiece(4, y, player.getKing());
		}
	}
	
	public void handleAction(JButton btn) {
		Point sq = (!switchBoard) ? new Point(btn.getX()/50, 7 - btn.getY()/50) : new Point(btn.getX()/50, btn.getY()/50);
		unhighlightSquares();
		if (board[sq.x][sq.y] != null && board[sq.x][sq.y].getPlayer() == active) {
			if (from != null) {
				unhighlightSquares();
				JButton old;
				if (!switchBoard)
					old = btns[from.x][from.y];
				else
					old = btns[from.x][7-from.y];
				old.setBorder(SQUARE_BORDER);
			}
			from = sq;
			highlightSquares(board[from.x][from.y].getSquares(false));
			if (board[from.x][from.y] instanceof King) {
				castleSquares = checkForCastle(active);
				highlightSquares(castleSquares);
			} else if (board[from.x][from.y] instanceof Pawn) {
				enPassantSquares = checkForEnPassant(active, board[from.x][from.y]);
				highlightSquares(enPassantSquares);
			}
			JButton newLoc;
			if (!switchBoard)
				newLoc = btns[from.x][from.y];
			else
				newLoc = btns[from.x][7-from.y];
			newLoc.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
		} else if (from != null) {
			boolean castling = checkForCastle(active).contains(sq);
			boolean enpassant = checkForEnPassant(active, board[from.x][from.y]).contains(sq);
			if (board[from.x][from.y].getSquares(false).contains(sq) || castling || enpassant) {
				Piece holder = board[sq.x][sq.y];
				int x1 = board[from.x][from.y].getX();
				setPiece(sq.x, sq.y, board[from.x][from.y]);
				removePiece(from.x, from.y);
				unhighlightSquares();
				
				Piece piece = board[sq.x][sq.y];
				if (castling && piece instanceof King) {
					if (sq.x > x1) {
						removePiece(piece.getPlayer().getKingRook().getX(), sq.y);
						setPiece(sq.x-1, sq.y, piece.getPlayer().getKingRook());
						logger.append("\n" + active.toString() + ": 0-0");
					} else {
						removePiece(piece.getPlayer().getQueenRook().getX(), sq.y);
						setPiece(sq.x+1, sq.y, piece.getPlayer().getQueenRook());
						logger.append("\n" + active.toString() + ": 0-0-0");
					}
				} else if (piece instanceof Pawn && (sq.y == 7 || sq.y == 0)) {
					piece = new Queen(active, this);
					setPiece(sq, piece);
				} else if (piece instanceof Pawn && enpassant)
					removePiece(board[last.x][last.y].getX(), board[last.x][last.y].getY());
				
				if (piece instanceof Pawn) {
					((Pawn) piece).setDoubleJumped(!piece.hasMoved());
				}
				piece.firstMove = false;
				last = sq;
				getPiece(sq).setPosition(sq.x, sq.y);
				
				if (!castling) {
					String separator = "-";
					if (holder != null) {
						holder.firstMove = false;
						separator = "x";
					}
					Player other = (active == white) ? black : white;
					String check = "";
					if (isInCheck(other.getKing().getX(), other.getKing().getY(), other, other.getKing()))
						check = "+";
					String type = "";
					if (!(getPiece(sq) instanceof Pawn))
						type = getPiece(sq).toString();
					logger.append("\n" + active.toString() + ": " + type + ((char) (from.y+65)) + (from.x+1) + separator + ((char) (sq.y+65)) + (sq.x+1) + check);
				}
				
				drawBoard();
				endTurn();
			}
		}
	}
	
	public boolean isInCheck(int x1, int y1, Player player, Piece pc) {
		Piece holder = board[x1][y1];
		Point oldPos = new Point(pc.getX(), pc.getY());
		movePiece(new Point(x1, y1), pc);
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Piece piece = board[x][y];
				if (piece != null && piece.getPlayer() != player && piece.canAttack(player.getKing())) {
					setPiece(x1, y1, holder);
					setPiece(oldPos, pc);
					return true;
				}
			}
		}
		setPiece(x1, y1, holder);
		setPiece(oldPos, pc);
		return false;
	}
	
	public ArrayList<Point> checkForCastle(Player p) {
		Piece k = p.getKing();
		if (k.hasMoved())
			return new ArrayList<Point>();
		if (isInCheck(k.getX(), k.getY(), p, k))
			return new ArrayList<Point>();
		ArrayList<Point> sq = new ArrayList<Point>();
		if (!p.getKingRook().hasMoved()) {
			int x = k.getX() + 1;
			if (board[x][k.getY()] == null && board[x+1][k.getY()] == null && !isInCheck(x,k.getY(),p,k) && !isInCheck(x+1,k.getY(),p,k))
				sq.add(new Point(x+1, k.getY()));
		}
		if (!p.getQueenRook().hasMoved()) {
			int x = k.getX() - 1;
			if (board[x][k.getY()] == null && board[x-1][k.getY()] == null && board[x-2][k.getY()] == null && !isInCheck(x,k.getY(),p,k) && !isInCheck(x-1,k.getY(),p,k))
				sq.add(new Point(x-1, k.getY()));
		}
		return sq;
	}
	
	public ArrayList<Point> checkForEnPassant(Player pl, Piece pawn) {
		if (last == null)
			return new ArrayList<Point>();
		
		Piece p = board[last.x][last.y];
		if (!(p instanceof Pawn) || !((Pawn) p).hasDoubleJumped())
			return new ArrayList<Point>();
		
		int yy = (active == white) ? 1 : -1;
		ArrayList<Point> sq = new ArrayList<Point>();
		if (p.getY() == pawn.getY()) {
			if (p.getX() == pawn.getX() + 1)
				sq.add(new Point(pawn.getX() + 1, pawn.getY() + yy));
			else if (p.getX() == pawn.getX() - 1)
				sq.add(new Point(pawn.getX() - 1, pawn.getY() + yy));
		}
		return sq;
	}
	
	public boolean isCheckmated(Player player) {
		boolean checked = false;
		for (int y = 0; y < 8 && !checked; y++) {
			for (int x = 0; x < 8 && !checked; x++) {
				Piece piece = board[x][y];
				if (piece != null && piece.getPlayer() != player && piece.canAttack(player.getKing()))
					checked = true;
			}
		}
		if (!checked)
			return false;
		
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Piece piece = board[x][y];
				if (piece != null && piece.getPlayer() == player) {
					ArrayList<Point> sqs = piece.getSquares(false);
					for (Point sq : sqs) {
						if (!isInCheck(sq.x, sq.y, player, board[x][y]))
							return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean isStalemated(Player player) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Piece piece = board[x][y];
				if (piece != null && piece.getPlayer() == player && piece.getSquares(false).size() > 0)
					return false;
			}
		}
		return true;
	}
	
	private void movePiece(Point sq, Piece piece) {
		removePiece(piece);
		setPiece(sq, piece);
	}
	
	public void removePiece(int x, int y) {
		board[x][y] = null;
	}
	
	public void removePiece(Piece piece) {
		board[piece.getX()][piece.getY()] = null;
	}
	
	public boolean isClear(int x, int y, Player player) {
		return board[x][y] == null || board[x][y].getPlayer() != player;
	}
	
	public boolean isOpponent(int x, int y, Player player) {
		return board[x][y] != null && board[x][y].getPlayer() != player;
	}
	
	public void setPiece(int x, int y, Piece piece) {
		board[x][y] = piece;
		if (piece != null) {
			piece.setPosition(x, y);
		}
	}
	
	public void setPiece(Point sq, Piece piece) {
		setPiece(sq.x, sq.y, piece);
	}
	
	public boolean hasPieceAtSquare(int x, int y) {
		return board[x][y] != null;
	}
	
	public Piece getPieceAtSquare(int x, int y) {
		return board[x][y];
	}
	
	private Piece getPiece(Point sq) {
		return board[sq.x][sq.y];
	}
}

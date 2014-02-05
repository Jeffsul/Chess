package com.jeffsul.chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.jeffsul.chess.Player.Colour;
import com.jeffsul.chess.pieces.King;
import com.jeffsul.chess.pieces.Pawn;
import com.jeffsul.chess.pieces.Piece;
import com.jeffsul.chess.pieces.Queen;

@SuppressWarnings("serial")
public class BoardPanel extends JPanel implements ActionListener {
	
	private static final int BOARD_WIDTH = 400;
	private static final int BOARD_HEIGHT = 400;
	
	private static final Border SQUARE_BORDER = BorderFactory.createLineBorder(Color.BLACK);
	private static final Border HIGHLIGHT_BORDER = BorderFactory.createLineBorder(Color.BLUE, 3);
	
	private static final Color LIGHT = Color.RED;
	private static final Color DARK = Color.GRAY;	
	
	private boolean switchBoard;
	private Board board;
	private JButton[][] btns;
	
	private boolean gameOver;
	
	private ArrayList<Point> castleSquares;
	private ArrayList<Point> enPassantSquares;
	
	private Point from;
	private Point last;

	public Player white;
	public Player black;
	public Player active;
	
	private ArrayList<Point> highlightedSquares;
	
	private Logger logger;
	
	public BoardPanel(Logger l) {
		logger = l;
		
		setLayout(new GridLayout(8, 8));
		setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
	}
	
	public void resetBoard() {
		removeAll();
		
		btns = new JButton[8][8];
		
		gameOver = false;
		from = null;
		last = null;
		
		white = new Player(Colour.WHITE);
		black = new Player(Colour.BLACK);
		board = new Board(white, black);
		active = white;
		
		switchBoard = false;
		
		highlightedSquares = new ArrayList<Point>();
		
		Dimension btnSize = new Dimension(100, 100);
		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
				btns[x][y] = new JButton("");
				btns[x][y].addActionListener(this);
				btns[x][y].setBorder(SQUARE_BORDER);
				btns[x][y].setPreferredSize(btnSize);
				add(btns[x][y]);
			}
		}
		
		drawBoard();
	}
	
	public void drawBoard() {
		int i = !switchBoard ? 0 : 1;
		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
				JButton btn = btns[x][y];
				
				int yy = !switchBoard ? y : 7 - y;
				if (board.hasPieceAtSquare(x, yy))
					btn.setIcon(board.getPieceAtSquare(x, y).getImage());
				else
					btn.setIcon(null);
				
				if (i % 2 == 0)
					btn.setBackground(LIGHT);
				else
					btn.setBackground(DARK);
				i++;
			}
			i = (i % 2 == 0) ? 1 : 0;
		}
		repaint();
	}
	
	private void highlightSquares(ArrayList<Point> squares) {
		for (Point sq : squares) {
			highlightedSquares.add(sq);
			if (!switchBoard)
				btns[sq.x][sq.y].setBorder(HIGHLIGHT_BORDER);
			else
				btns[sq.x][7 - sq.y].setBorder(HIGHLIGHT_BORDER);
		}
	}
	
	private void unhighlightSquares() {
		for (Point sq : highlightedSquares) {
			if (!switchBoard)
				btns[sq.x][sq.y].setBorder(SQUARE_BORDER);
			else
				btns[sq.x][7 - sq.y].setBorder(SQUARE_BORDER);
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		if (gameOver)
			return;
		
		if (event.getSource() instanceof JButton) {
			board.handleAction((JButton) event.getSource());
		}
	}
	
	public void switchSides() {
		switchBoard = !switchBoard;
		unhighlightSquares();
		drawBoard();
	}
	
	private void endTurn() {
		JButton fromBtn = !switchBoard ? btns[from.x][from.y] : btns[from.x][7 - from.y];
		fromBtn.setBorder(SQUARE_BORDER);
		from = null;
		Player past = active;
		active = (active == white) ? black : white;
		if (board.isInCheck(active.getKing().getX(), active.getKing().getY(), active, active.getKing()) && board.isCheckmated(active)) {
			gameOver = true;
			logger.append("+");
			JOptionPane.showMessageDialog(this, past.toString() + " wins the game!", "Checkmate!", JOptionPane.INFORMATION_MESSAGE);
		} else if (board.isStalemated(active)) {
			gameOver = true;
			JOptionPane.showMessageDialog(this, "The players have drawn with a stalemate!", "Stalemate!", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}

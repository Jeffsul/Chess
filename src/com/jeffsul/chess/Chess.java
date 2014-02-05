package com.jeffsul.chess;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class Chess extends JFrame {
	
	private BoardPanel boardPnl;
	
	private JPanel optionPnl;
	private JTextArea log;
	private JScrollPane sp;
	
	public Chess() {
		super("Chess 4.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());
		
		boardPnl = new BoardPanel(new Logger(log));
		add(boardPnl, BorderLayout.CENTER);
		
		optionPnl = new JPanel();
		optionPnl.setLayout(new BoxLayout(optionPnl, BoxLayout.Y_AXIS));
		
		JButton newGameBtn = new JButton("New Game");
		newGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//initGame();
				boardPnl.resetBoard();
			}
		});
		optionPnl.add(newGameBtn);
		
		JButton switchSideBtn = new JButton("Switch Side");
		switchSideBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				boardPnl.switchSides();
			}
		});
		optionPnl.add(switchSideBtn);
		
		add(optionPnl, BorderLayout.PAGE_END);
		
		pack();
		initGame();
		setVisible(true);
	}
	
	private void initGame() {
		boardPnl.resetBoard();
		
		if (log != null) {
			optionPnl.remove(sp);
		}
		sp.remove(log);
		
		log = new JTextArea(5, 5);
		log.setEditable(false);
		log.append("Game started.");
		sp = new JScrollPane(log);
		optionPnl.add(sp);
		
		pack();
	}
	
	public static void main(String[] args) { new Chess(); }
}

class Logger {
	private JTextArea log;
	
	public Logger(JTextArea log) {
		this.log = log;
	}
	
	public void append(String s) {
		log.append(s);
	}
}

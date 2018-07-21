package main;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;


	@SuppressWarnings("serial")
	public class Main extends JFrame implements Runnable {
		private Image dbImage;
		private Graphics dbg;
		
		final int SCREEN_WIDTH = 500;
		final int SCREEN_HEIGHT = 500;
		final int PLAYER_WIDTH = 10;
		final int PLAYER_HEIGHT = 40;
		final int BALL_X = 6;
		final int BALL_Y = 5;
		public MyRectangle playerOne, playerTwo, ball, invisibleBall;
		String string = "";
		boolean lost = false;
		int mx, my, difficultyCenter, level, randColor, fps;
		FontMetrics font;
		
		int collisionSide;
		int collisionSideTwo;
		int invisibleCollision;
		boolean playerOnePoint = false;
		boolean playerTwoPoint = false;
		int playerTwoPoints = 0;
		int playerOnePoints = 0;
		int yError;
		boolean mouseClicked = false;
		Color startHover, difficultyHover;
		boolean lose = false;
		
		int losing = 80;
		int winning = 20;
		
		ArrayList<Color> colors = new ArrayList<Color>();
		
		GameState state = new GameState(0);
		ArrayList<String> difficulty = new ArrayList<String>();
		
		public Main() {
			setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
			setTitle("Box Collision");
			setResizable(false);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			addKeyListener(new AL());
			addMouseListener(new ML());
			addMouseMotionListener(new MM());
			colors.addAll(Arrays.asList(Color.red, Color.blue, Color.yellow, Color.CYAN, Color.pink, Color.magenta 
					, Color.green, Color.orange));
			
			playerOne = new MyRectangle(PLAYER_WIDTH, PLAYER_HEIGHT, 10, 10);
			playerTwo = new MyRectangle(PLAYER_WIDTH, PLAYER_HEIGHT, SCREEN_WIDTH - PLAYER_WIDTH - 10 , 
					SCREEN_HEIGHT - PLAYER_HEIGHT - 10);
			ball = new MyRectangle(5, 5, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
			ball.dx = BALL_X;
			ball.dy = BALL_Y;
			invisibleBall = new MyRectangle(5, 5, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
			invisibleBall.x = ball.x;
			invisibleBall.y = ball.y;
			invisibleBall.dx = ball.dx;
			invisibleBall.dy = ball.dy;
			
			state.setState(1);
			difficulty.add("Easy");
			difficulty.add("Medium");
			difficulty.add("Hard");
			level = 0;
			startHover = Color.white;
			difficultyHover = Color.white;
			
			randColor = (int)(Math.random() * 8);
			playerOne.color = colors.get(randColor);
			
			randColor = (int)(Math.random() * 8);
			playerTwo.color = colors.get(randColor);
			
			ball.color = playerTwo.color;

		}
		
		public class MM extends MouseMotionAdapter {
			public void mouseMoved(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
			}
		}
		
		public class ML extends MouseAdapter {
			public void mousePressed(MouseEvent e) {
				mouseClicked = true;
			}
			
			public void mouseReleased(MouseEvent e) {
				mouseClicked = false;
			}
		}
		
		public class AL extends KeyAdapter {
			@SuppressWarnings("static-access")
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if(keyCode == e.VK_UP)
					setYDirectionOne(-5);
				if(keyCode == e.VK_DOWN) 
					setYDirectionOne(5);
				if(keyCode == e.VK_W)
					setYDirectionTwo(-5);
				if(keyCode == e.VK_S)
					setYDirectionTwo(5);
			}
			@SuppressWarnings("static-access")
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if(keyCode == e.VK_UP)
					setYDirectionOne(0);
				if(keyCode == e.VK_DOWN) 
					setYDirectionOne(0);
				if(keyCode == e.VK_W)
					setYDirectionTwo(0);
				if(keyCode == e.VK_S)
					setYDirectionTwo(0);
			}
			
		}
		
		public void run() {
			try {
				while(true) {
					if(state.getState() == 1) {
						drawMainMenu();
					} else if(state.getState() == 2)
						move();
					else if(state.getState() == 3)
						loseGame();
					
					Thread.sleep(fps);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public void move() {
			playerOne.y += playerOne.dy; 
			playerTwo.y += playerTwo.dy;
			
			ball.x += ball.dx;
			ball.y += ball.dy;
			invisibleBall.x += invisibleBall.dx;
			invisibleBall.y += invisibleBall.dy;
			
			if(invisibleBall.y > playerTwo.y + 1 && invisibleBall.y < playerTwo.y + (playerTwo.height / 2)) {
				playerTwo.dy = 0;
			} else if(invisibleBall.y > playerTwo.y){
				playerTwo.dy = 5;
			} else if(invisibleBall.y < playerTwo.y + 5) {
				playerTwo.dy = -5;
			}
			
			//Screen Boundaries 
			if(ball.x <= 0) 
				playerTwoPoint = true;
			else if(ball.x >= SCREEN_WIDTH - ball.width)
				playerOnePoint = true;
			
			if(ball.y <= 0) {
				ball.y = 0;
				ball.dy = -ball.dy;
			}
			else if(ball.y >= SCREEN_HEIGHT - ball.height ) {
				ball.y = SCREEN_HEIGHT - ball.height ;
				ball.dy = -ball.dy;
			}
			
			if(playerOne.y <= 0) {
				playerOne.y = 0;
			}
			if(playerOne.y + playerOne.height >= SCREEN_HEIGHT) {
				playerOne.y = SCREEN_HEIGHT - playerOne.height;
			}
			if(playerTwo.y <= 0) {
				playerTwo.y = 0;
			}
			if(playerTwo.y + playerTwo.height >= SCREEN_HEIGHT) {
				playerTwo.y = SCREEN_HEIGHT - playerTwo.height;
			}
			if(invisibleBall.y <=0) {
				invisibleBall.y = 0;
				invisibleBall.dy *= -1;
			}
			if(invisibleBall.y >= SCREEN_HEIGHT - invisibleBall.height) {
				invisibleBall.y = SCREEN_HEIGHT - invisibleBall.height;
				invisibleBall.dy *= -1;
			}
			
			
			
			
			collisionSide = ball.blockRectangle(ball, playerOne);
			collisionSideTwo = playerTwo.blockRectangle(ball, playerTwo);
			if(collisionSide != 0) {
				if(playerTwoPoints > playerOnePoints)
					yError = (int)(Math.random() * losing);
				else
					yError = (int)(Math.random() * winning);
				ball.dx = -ball.dx;
				invisibleBall.x = ball.x;
				invisibleBall.y = ball.y + yError;
				invisibleBall.dx = ball.dx * 2;
				invisibleBall.dy = ball.dy * 2;
				ball.color = playerOne.color;
			} else if (collisionSideTwo != 0) {
				ball.dx = -ball.dx;
				ball.color = playerTwo.color;
			} else if(collisionSide == 1 || collisionSide == 2) {
				ball.x -= 5;
			} else if(collisionSideTwo == 1 || collisionSideTwo == 2) {
				ball.x += 5;
			}
			
			if(invisibleBall.x > playerTwo.x) {
				invisibleBall.x = playerTwo.x - invisibleBall.width;
				invisibleBall.dy = 0;
				invisibleBall.dx = 0;
			}
			
			if(playerTwoPoint) {
				ball.dx = -1 * (int )(Math.random() * 5 + 4);
				ball.dy = -1 * (int )(Math.random() * 4 + 1);
				ball.x = SCREEN_WIDTH - 40;
				playerTwoPoints++;
				playerTwoPoint = false;
			}
			if(playerOnePoint) {
				ball.dx = (int )(Math.random() * 5 + 4);
				ball.dy = (int )(Math.random() * 4 + 1);
				ball.x = 40;
				playerOnePoints++;
				playerOnePoint = false;
				invisibleBall.x = ball.x;
				invisibleBall.y = ball.y;
				invisibleBall.dx = ball.dx * 2;
				invisibleBall.dy = ball.dy * 2;
			}
			
			if(playerOnePoints >= 11) {
				string = "Player One Wins!";
				state.setState(3);
				lose = true;
			} else if (playerTwoPoints >= 11) {
				string = "Player Two wins!";
				state.setState(3);
				lose = true;
			}
			
		}
		
		
		public void setYDirectionOne(int yDir) {
			playerOne.dy = yDir;
		}
		
		public void setYDirectionTwo(int yDir) {
			playerTwo.dy = yDir;
		}
		
		
		public void drawMainMenu() { 
			if(font != null) {
				if(mx >= (SCREEN_WIDTH / 2) - (font.stringWidth("Difficulty: " + difficulty.get(level)) / 2) 
						&& mx <= (SCREEN_WIDTH / 2) + (font.stringWidth("Difficulty: " + difficulty.get(level)) / 2) 
						&& my >= 150 && my <= 240) {
					difficultyHover = Color.gray;
					if(mouseClicked) {
						if(level < 2) {
							level++;
							mouseClicked = false;
							difficultyHover = Color.gray;
						} else if (level >= 2){
							level = 0;
							mouseClicked = false;
						}
					}
				} else {
					difficultyHover = Color.white;
				}
				
				if(mx >= (SCREEN_WIDTH / 2) - (font.stringWidth("Start") / 2) && mx <= (SCREEN_WIDTH / 2) 
						+ (font.stringWidth("Start") / 2) && my >= 40 && my <= 100) {
					startHover = Color.gray;
					if(mouseClicked) {
						state.setState(2);
						mouseClicked = false;
					}
				} else {
					startHover = Color.white;
				}
			}
			
			if(level == 0) {
				losing = 80;
				winning = 20;
				fps = 30;
			} else if (level == 1) {
				losing = 60;
				winning = 20;
				fps = 25;
			} else if (level == 2) {
				losing = 30;
				winning = 5;
				fps = 20;
			}
		}
		
		public void loseGame() {
			if(mouseClicked) 
				state.setState(1);
		}
		
		public void paint(Graphics g) {
			dbImage = createImage(getWidth(), getHeight());
			dbg = dbImage.getGraphics();
			paintComponent(dbg);
			g.drawImage(dbImage, 0, 0, this);
		}
		
		public void paintComponent(Graphics g) {
			
			
			if(state.getState() == 1) {
				g.setColor(Color.black);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				g.setColor(startHover);
				g.setFont(new Font("Monaco", Font.PLAIN, 40));
				font = g.getFontMetrics();
				g.drawString("Start", (SCREEN_WIDTH / 2) - (font.stringWidth("Start") / 2), 100);
				
				g.setColor(difficultyHover);
				difficultyCenter = font.stringWidth("Difficulty: " + difficulty.get(level)) / 2;
				g.drawString("Difficulty: " + difficulty.get(level), (SCREEN_WIDTH / 2) - difficultyCenter, 200);
			} else if(state.getState() == 2) {
				g.setColor(Color.black);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				g.setColor(playerOne.color);
				g.fillRect(playerOne.x, playerOne.y, playerOne.width, playerOne.height);
				
				g.setColor(playerTwo.color);
				g.fillRect(playerTwo.x, playerTwo.y, playerTwo.width, playerTwo.height);
				
				g.setColor(ball.color);
				g.fillRect(ball.x, ball.y, ball.width, ball.height);
				
				g.setColor(Color.white);
				g.drawRect(-5, -5, SCREEN_WIDTH / 2, SCREEN_HEIGHT + 10);
				
				//g.setColor(Color.red);
				//g.fillRect(invisibleBall.x, invisibleBall.y, invisibleBall.width, invisibleBall.height);
				
				g.setColor(playerOne.color);
				g.drawString(Integer.toString(playerOnePoints), 50, 50);
				
				g.setColor(playerTwo.color);
				g.drawString(Integer.toString(playerTwoPoints), SCREEN_WIDTH - 50, 50);
			} else if (lose) {
				g.setFont(new Font("Monaco", Font.PLAIN, 40));
				g.drawString(string, 100, 100);
			}
			
			repaint();
		}
		
		public static void main(String[] args) {
			Main jg = new Main();
			
			Thread t1 = new Thread(jg);
			t1.start();
		}
	}	


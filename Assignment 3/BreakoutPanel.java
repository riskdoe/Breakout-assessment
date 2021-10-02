import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class BreakoutPanel extends JPanel implements ActionListener, KeyListener {
	
	static final long serialVersionUID = 2L;

	private boolean gameRunning = true;
	private int livesLeft = 3;
	private String screenMessage = "";
	private Ball ball;
	private Paddle paddle;
	private Brick bricks[];
	
	//custom value just to make movement not feel as jank
	Boolean isleftPressed = false;
	Boolean isrightPressed = false;
	
	public BreakoutPanel(Breakout game) {
		
		addKeyListener(this);
		setFocusable(true);
		
		Timer timer = new Timer(5, this);
		timer.start();
		
		// TODO: Create a new ball object and assign it to the appropriate variable
		ball = new Ball();
		// TODO: Create a new paddle object and assign it to the appropriate variable
		paddle = new Paddle();
		// TODO: Create a new bricks array (Use Settings.TOTAL_BRICKS)
		bricks = new Brick[Settings.TOTAL_BRICKS];
		// TODO: Call the createBricks() method
		createBricks();
	}
	
	private void createBricks() {
		int counter = 0;
		int x_space = 0;
		int y_space = 0;
		for(int x = 0; x < 4; x++) {
			for(int y = 0; y < 5; y++) {
				bricks[counter] = new Brick((x * Settings.BRICK_WIDTH) + Settings.BRICK_HORI_PADDING + x_space, (y * Settings.BRICK_HEIGHT) + Settings.BRICK_VERT_PADDING + y_space);
				counter++;
				y_space++;
			}
			x_space++;
			y_space = 0;
		}
	}
	
	private void paintBricks(Graphics g) {
		// TODO: Loop through the bricks and call the paint() method
		for(int i = 0; i < bricks.length; i++) {
		bricks[i].paint(g);
		}
		
	}
	
	private void update() {
		if(gameRunning) {
			// TODO: Update the ball and paddle
			ball.update();
			paddle.update();
			
			//handling movement of paddle here to allow for program to
			//handle left and right pressed at same time
			int SPEED = Settings.BALL_VELOCITY * 2;
			if (isleftPressed && isrightPressed) {
				paddle.setXVelocity(0);
			}
			else if (isleftPressed) {
				paddle.setXVelocity(-SPEED);
			}
			else if (isrightPressed) {
				paddle.setXVelocity(SPEED);
			}
			else if(!isleftPressed || !isrightPressed) {
				paddle.setXVelocity(0);
			}
			
			collisions();
			repaint();
		}
	}
	
	private void gameOver() {
		// TODO: Set screen message
		screenMessage = "Game Over";
		stopGame();
	}
	
	private void gameWon() {
		// TODO: Set screen message
		screenMessage = "You Win!";
		stopGame();
	}
	
	private void stopGame() {
		gameRunning = false;
	}
	
	private void collisions() {
		// Check for loss
		if(ball.y > 450) {
			// Game over
			livesLeft--;
			if(livesLeft <= 0) {
				gameOver();
				return;
			} else {
				ball.resetPosition();
				ball.setYVelocity(-1);
			}
		}
		
		// Check for win
		
		boolean bricksLeft = false;
		for(int i = 0; i < bricks.length; i++) {
			// Check if there are any bricks left
			if(!bricks[i].isBroken()) {
				// Brick was found, close loop
				bricksLeft = true;
				break;
			}
		}
		if(!bricksLeft) {
			gameWon();
			return;
		}
		
		// Check collisions
		if(ball.getRectangle().intersects(paddle.getRectangle())) {
			// Simplified touching of paddle
			// Proper game would change angle of ball depending on where it hit the paddle
			ball.setYVelocity(-Settings.BALL_VELOCITY);
		}
		
		for(int i = 0; i < bricks.length; i++) {
			if (ball.getRectangle().intersects(bricks[i].getRectangle())) {
				int ballLeft = (int) ball.getRectangle().getMinX();
	            int ballHeight = (int) ball.getRectangle().getHeight();
	            int ballWidth = (int) ball.getRectangle().getWidth();
	            int ballTop = (int) ball.getRectangle().getMinY();

	            Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
	            Point pointLeft = new Point(ballLeft - 1, ballTop);
	            Point pointTop = new Point(ballLeft, ballTop - 1);
	            Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

	            if (!bricks[i].isBroken()) {
	                if (bricks[i].getRectangle().contains(pointRight)) {
	                    ball.setXVelocity(-Settings.BALL_VELOCITY);
	                } else if (bricks[i].getRectangle().contains(pointLeft)) {
	                    ball.setXVelocity(Settings.BALL_VELOCITY);
	                }

	                if (bricks[i].getRectangle().contains(pointTop)) {
	                    ball.setYVelocity(Settings.BALL_VELOCITY);
	                } else if (bricks[i].getRectangle().contains(pointBottom)) {
	                    ball.setYVelocity(-Settings.BALL_VELOCITY);
	                }
	                bricks[i].setBroken(true);
	            }
			}
		}
		
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ball.paint(g);
        paddle.paint(g);
        paintBricks(g);
        
        // Draw lives left
        // TODO: Draw lives left in the top left hand corner
        int xPadding = 10;
        int yPadding = 30;
        int fontSize = 30;
        Font Fontlife = new Font("Serif", Font.BOLD, fontSize);
        String lifes = Integer.toString(livesLeft);
        g.setFont(Fontlife);
        g.drawString(lifes, xPadding, yPadding);
        
        
        // Draw debug information
        
        if (Settings.debugMode) {
            yPadding = 15;
            fontSize = 15;
            Font Fontdebug = new Font("Serif", Font.BOLD, fontSize);
            String debuginfo = "Ball X: " + Integer.toString(ball.getX()) + "Ball Y: " + Integer.toString(ball.getY());
            g.setFont(Fontdebug);
            int debuginfowidth = g.getFontMetrics().stringWidth(debuginfo);
            g.drawString(debuginfo, (Settings.WINDOW_WIDTH / 2) - (debuginfowidth / 2), yPadding);
            debuginfo = "Paddle X: " + Integer.toString(paddle.getX());
            debuginfowidth = g.getFontMetrics().stringWidth(debuginfo);
            g.drawString(debuginfo, (Settings.WINDOW_WIDTH / 2) - (debuginfowidth / 2), yPadding * 2);
            debuginfo = "Ball Velocity X: " + Integer.toString(ball.getXVelocity()) + "Ball Velocity Y: " + Integer.toString(ball.getYVelocity());
            debuginfowidth = g.getFontMetrics().stringWidth(debuginfo);
            g.drawString(debuginfo, (Settings.WINDOW_WIDTH / 2) - (debuginfowidth / 2), yPadding * 3);
        }

        
        // Draw screen message
        if(screenMessage != null) {
        	g.setFont(new Font("Arial", Font.BOLD, 18));
        	int messageWidth = g.getFontMetrics().stringWidth(screenMessage);
        	g.drawString(screenMessage, (Settings.WINDOW_WIDTH / 2) - (messageWidth / 2), Settings.MESSAGE_POSITION);
        }
    }

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO: Set the velocity of the paddle depending on whether the player is pressing left or right
		//check update() for the change of Velocity as i have rewriten this to make it smoother
		int keyCode = e.getKeyCode();
		
		if (keyCode == KeyEvent.VK_LEFT) {
			isleftPressed = true;
		}
		if (keyCode == KeyEvent.VK_RIGHT) {
			isrightPressed = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO: Set the velocity of the paddle after the player has released the keys
		//check update() for the change of Velocity as i have rewriten this to make it smoother
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT) {
			isleftPressed = false;
		}
		if (keyCode == KeyEvent.VK_RIGHT) {
			isrightPressed = false;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		update();
	}

}

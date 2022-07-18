import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JButton;
import java.awt.EventQueue;

public class CircToCircS1 {
	
	CircObject c1,c2;
	boolean isColliding;
	float c1CenterX,c1CenterY,c2CenterX,c2CenterY;
	float mMotionX,mMotionY;
	
	public static void main(String[] args) {
		new CircToCircS1();
	}
	
	public CircToCircS1() {
		
		c1 = new CircObject(200f,100f,40f,40f);
		c2 = new CircObject(150f,150f,80f,80f);
		
		EventQueue.invokeLater(new Runnable(){
			
			@Override
			public void run() {
				JFrame jf = new JFrame("CircToCircS1");
				Panel pnl = new Panel(new BorderLayout());
				JPanel sPnl = new JPanel();
				JButton btn1 = new JButton("Check for collision");
				jf.add(pnl);
				pnl.addMouseMotionListener(new MouseMotion());
				btn1.addActionListener(new btn1Action());
				btn1.setAlignmentX(150f);
				btn1.setAlignmentY(300f);
				sPnl.add(btn1);
				pnl.add(sPnl, BorderLayout.SOUTH);
				jf.pack();
				jf.setResizable(false);
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jf.setLocationRelativeTo(null);
				jf.setVisible(true);
			}
			
		});
		
	}
	
	void updateData(){
		//System.out.println("updating...");
		c1CenterX = c1.getX() + c1.getWidth() * 0.5f;
		c1CenterY = c1.getY() + c1.getHeight() * 0.5f;
		c2CenterX = c2.getX() + c2.getWidth() * 0.5f;
		c2CenterY = c2.getY() + c2.getHeight() * 0.5f;
	}
	
	void drawObjects(Graphics2D g2d){
		//System.out.println("drawing objects...");
		g2d.setPaint(Color.GREEN);
		g2d.fill(c2.getCirc());
		g2d.setPaint(Color.YELLOW);
		//update the positions of each circle. Their positions may change due to
		//a collision
		c1.getCirc().setFrame(c1.getX(),c1.getY(),c1.getWidth(),c1.getHeight());
		c2.getCirc().setFrame(c2.getX(),c2.getY(),c2.getWidth(),c2.getHeight());
		g2d.fill(c1.getCirc());
	}
	
	float checkCollision(){
		
		//circle-to-circle collision
		float totalSq = (c1.getRadX() + c2.getRadX()) * (c1.getRadX() + c2.getRadX());
		float centerSqX = (c1CenterX - c2CenterX) * (c1CenterX - c2CenterX);
		float centerSqY = (c1CenterY - c2CenterY) * (c1CenterY - c2CenterY);
		float totalCenterSq = centerSqX + centerSqY;
		if(totalCenterSq < totalSq){
			isColliding = true;
			return totalCenterSq; 
		}
		else{
			isColliding = false;
			return 0f;
		}
		
	}
	
	//Method #1
	//by angle
	void separateCircs1(float radius) {
		
		System.out.println("Before");
		System.out.println("c1X: " + c1.getX() + ", " + "c1Y: " + c1.getY());
		System.out.println("c2X: " + c2.getX() + ", " + "c2Y: " + c2.getY());
		System.out.println();
		
		//we're using arctangent to get the angle of the
		//right triangle in-between two centers.
		//x is adjacent to the radius(hypotenuse) so denote x as adjacent
		//y is opposite to the radius(hypotenuse) so denote y as opposite
		float adjacent = Math.abs(c1CenterX - c2CenterX);
		float opposite = Math.abs(c1CenterY - c2CenterY);
		float angle = 0f;
		
		//if adjacent is 0 then two centers are perfectly aligned in
		//y-axis. The problem here is that we can't divide something by
		//0 so better assign 90 degrees(1.5708 rad) because sin(90)=1
		//and cos(90)=0 which are the values that we need if a right triangle is
		//perfectly aligned in y-axis. We don't need to worry if opposite is 0
		//'cause atan(0/1)=0 and sin(0)=0 and cos(0)=1 which are the values that we
		//need if a right triangle is perfectly aligned in y-axis
		if(adjacent == 0f) angle = 1.5708f;
		else angle = (float)Math.atan(opposite/adjacent);
		
		//get the sin() and cos() of the angle
		float cosx = (float) Math.cos(angle);
		float siny = (float) Math.sin(angle);
		
		//get the radius of the right triangle in-between center points
		//we need the radius length so the squared radius that we're using to
		//compare if two circles collide won't work here. We better use square root
		float centerRadius = (float)Math.sqrt(radius);
		//get the total radius of both circles. Whether radius-x or radius-y
		//would suffice
		float totalRadius = c1.getRadX() + c2.getRadX() + 0.5f;
		//get the overlap radius between two circles
		float overlap = totalRadius - centerRadius;
		//If you want to add an allowance to separating circles, just add
		//a little amount that you think is enough allowance to the equation
		//float overlap = totalRadius - centerRadius + 1f;
		//or this equation
		//e.g float totalRadius = c1.getRadX() + c2.getRadX() + 1f;
		//1f is the allowance
		
		//set signs to indicate which direction the circles would move
		int xSign = 1;
		int ySign = 1;
		//we will check the center of c1 and assign which direction
		//it would go. For c2, we just need to invert the direction
		//which c1 is going to take
		if(c1CenterX > c2CenterX) xSign = 1;
		else xSign = -1;
		if(c1CenterY > c2CenterY) ySign = 1;
		else ySign = -1;
		//add the move distance that c1 needs in order to move out
		//of collision. I want the two circles to move so I need to 
		//divide the move distance by 2(or * 0.5f) and distribute it to two circles 
		c1.setX(c1.getX() + cosx * overlap * 0.5f * xSign);
		c1.setY(c1.getY() + siny * overlap * 0.5f * ySign);
		//Invert the xSign and ySign to make c2 move to opposite direction
		//of the c1
		c2.setX(c2.getX() + cosx * overlap * 0.5f * -xSign);
		c2.setY(c2.getY() + siny * overlap * 0.5f * -ySign);
		
		//This code only moves c1
		//c1.setX(c1.getX() + cosx * overlap * xSign);
		//c1.setY(c1.getY() + siny * overlap * ySign);
		
		System.out.println("After");
		System.out.println("c1X: " + c1.getX() + ", " + "c1Y: " + c1.getY());
		System.out.println("c2X: " + c2.getX() + ", " + "c2Y: " + c2.getY());
		System.out.println();
	}
	
	//Method #2
	//By Midpoint
	void separateCircs2(float radius){
		float midX,midY;
		
		//find the midpoint between two circles' centers
		midX = (c1CenterX + c2CenterX) * 0.5f;
		midY = (c1CenterY + c2CenterY) * 0.5f;
		System.out.println("midpointX: " + midX + ", midpointY: " + midY);
		
		//get the radius between centers
		//If you want to add allowance to the separation of circles just reduce
		//by a little amount that you think is enough allowance to the equation
		//remember, In this equation, the shorter the center radius, the higher
		//the separation value
		//e.g float centerRadius = (float)Math.sqrt(radius - 1f);
		//1f is the allowance
		float centerRadius = (float)Math.sqrt(radius - 0.5f);
		System.out.println("Before");
		System.out.println("c1X: " + c1.getX() + ", " + "c1Y: " + c1.getY());
		System.out.println("c1CenterX: " + c1CenterX + ", " + "c1CenterY: " + c1CenterY);
		System.out.println("c2X: " + c2.getX() + ", " + "c2Y: " + c2.getY());
		System.out.println("c2CenterX: " + c2CenterX + ", " + "c2CenterY: " + c2CenterY);
		System.out.println();
		
		//normalize x-length and y-length in-between centers then multiply it by radius-x of each circle
		//then add the result to the midpoint.
		float c1CenterXNew = midX + c1.getRadX() * (c1CenterX - c2CenterX) / centerRadius;
		float c1CenterYNew = midY + c1.getRadY() * (c1CenterY - c2CenterY) / centerRadius;
		float c2CenterXNew = midX + c2.getRadX() * (c2CenterX - c1CenterX) / centerRadius;
		float c2CenterYNew = midY + c2.getRadY() * (c2CenterY - c1CenterY) / centerRadius;
		//move the "spawn point" of each circle by subtracting new centers to the radii
		c1.setX(c1CenterXNew - c1.getRadX());
		c1.setY(c1CenterYNew - c1.getRadY());
		c2.setX(c2CenterXNew - c2.getRadX());
		c2.setY(c2CenterYNew - c2.getRadY());
		System.out.println("After");
		System.out.println("c1X: " + c1.getX() + ", " + "c1Y: " + c1.getY());
		System.out.println("c1CenterX: " + c1CenterXNew + ", " + "c1CenterY: " + c1CenterYNew);
		System.out.println("c2X: " + c2.getX() + ", " + "c2Y: " + c2.getY());
		System.out.println("c2CenterX: " + c2CenterXNew + ", " + "c2CenterY: " + c2CenterYNew);
		System.out.println();
		
	}
	
	//Method #3
	//by vector normalization
	void separateCircs3(float radius){
		//This method is similar to "midpoint" method with
		//few changes
		float length_x = c1CenterX - c2CenterX;
		float length_y = c1CenterY - c2CenterY;
		//If you want to add an allowance to separating circles, just add
		//a little amount that you think is enough allowance to the equation
		//example: c1.getRadX() + c2.getRadX() + 0.25f
		//0.25f is the allowance
		float totalRadius_x = c1.getRadX() + c2.getRadX() + 0.25f;
		float totalRadius_y = c1.getRadY() + c2.getRadY() + 0.25f;
		float centerRadius = (float)Math.sqrt(radius);
		
		float norm_x = length_x / centerRadius;
		float norm_y = length_y / centerRadius;
		
		float c1CenterXNew = c2CenterX + totalRadius_x * norm_x;
		float c1CenterYNew = c2CenterY + totalRadius_y * norm_y;
		
		c1.setX(c1CenterXNew - c1.getRadX());
		c1.setY(c1CenterYNew - c1.getRadY());
	}
	
	class Panel extends JPanel {
		
		Panel(BorderLayout layout){
			
			setLayout(layout);
			
			Timer timer = new Timer(16, new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e){
					updateData();
					repaint();
				}
			});
			timer.start();
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400,400);
		}
		
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setPaint(Color.BLACK);
			g2d.fillRect(0,0,getWidth(),getHeight());
			drawObjects(g2d);
			g2d.setPaint(Color.WHITE);
			g2d.drawString("Drag the yellow circle and make a collision", 60f, 20f);
			g2d.drawString("Click the button to check for collision", 60f, 40f);
			g2d.drawLine((int)c1CenterX,(int)c1CenterY,(int)c2CenterX,(int)c2CenterY);
			g2d.dispose();
		}
	}
	
	class CircObject {
		private float x,y,width,height;
		private Ellipse2D circ;
		
		CircObject(float x, float y, float width, float height){
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			circ = new Ellipse2D.Float(x,y,width,height);
		}
		
		float getX(){return x;}
		float getY(){return y;}
		float getWidth(){return width;}
		float getHeight(){return height;}
		float getRadX(){return width * 0.5f;}
		float getRadY(){return height * 0.5f;}
		Ellipse2D getCirc(){return circ;}
		
		void setX(float x){this.x = x;}
		void setY(float y){this.y = y;}
	}
	
	//Note: mouseDragged and mouseMoved don't work at the same time
	//when the program detects you're dragging your mouse then
	//the program only executes mouseDragged(). Otherwise, it only
	//executes mouseMoved(). That's why we can get the previous mouse
	//position before mouseDragged executes by getting the coordinates
	//of the mouse while it's moving without any drag motion.
	class MouseMotion implements MouseMotionListener {
	
		@Override
		public void mouseDragged(MouseEvent e){
			//point-to-circle collision
			float totalSq = c1.getRadX() * c1.getRadX();
			float centerSqX = (e.getX() - c1CenterX) * (e.getX() - c1CenterX);
		    float centerSqY = (e.getY() - c1CenterY) * (e.getY() - c1CenterY);
			float totalCenterSq = centerSqX + centerSqY;
		    if(totalCenterSq < totalSq){
				c1.setX(c1.getX() + (e.getX() - mMotionX));
				c1.setY(c1.getY() + (e.getY() - mMotionY));
				//When in drag state, the previous coordinates while mouse was
				//moving set an initial point that we can use as reference point.
				//We need to update the reference point in the drag state 'cause 
				//mouseMoved is not active while mouseDragged is active.
				mMotionX = e.getX();
				mMotionY = e.getY();
			}
		}
	
		@Override
		public void mouseMoved(MouseEvent e){
			//get mouse coordinates while moving so when
			//we drag the mouse then we can use the coordinates
			//here as previous coordinates
			mMotionX = e.getX();
			mMotionY = e.getY();
		}
	}
	
	class btn1Action implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e){
			
			float radius = checkCollision();
			
			if(isColliding){
			System.out.println("There's a collision");
			separateCircs1(radius);
			//separateCircs2(radius);
			//separateCircs3(radius);
			}
			else System.out.println("No collision");
			
		}
	}
	
}


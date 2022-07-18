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

public class CircCircS {
	
	CircObject c1,c2;
	boolean isColliding;
	float c1CenterX,c1CenterY,c2CenterX,c2CenterY;
	float mMotionX,mMotionY;
	
	public static void main(String[] args) {
		new CircCircS();
	}
	
	public CircCircS() {
		
		c1 = new CircObject(200f,100f,40f,40f);
		c2 = new CircObject(150f,150f,80f,80f);
		
		EventQueue.invokeLater(new Runnable(){
			
			@Override
			public void run() {
				JFrame jf = new JFrame("CircCircS");
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
		c2.getCirc().setFrame(c2.getX(),c2.getY(),c2.getWidth(),c2.getHeight());
		g2d.fill(c2.getCirc());
		g2d.setPaint(Color.YELLOW);
		c1.getCirc().setFrame(c1.getX(),c1.getY(),c1.getWidth(),c1.getHeight());
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
	
	void separateCircs(float radius){
		//put circle separation algorigthm here...
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
				mMotionX = e.getX();
				mMotionY = e.getY();
			}
		}
	
		@Override
		public void mouseMoved(MouseEvent e){
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
			separateCircs(radius);
			}
			else System.out.println("No collision");
			
		}
	}
	
}


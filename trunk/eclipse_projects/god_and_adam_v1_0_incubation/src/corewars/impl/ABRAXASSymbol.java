package corewars.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

public class ABRAXASSymbol extends JPanel {
//	private static final double SQRT_2_DIV_2 = Math.sqrt(2)/2;
	private final int[] xp=new int[5];
	private final int[] yp=new int[5];
	public ABRAXASSymbol(){
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				repaint();
			}
		});
		repaint();
	}
	public void paint(Graphics g){
		Rectangle r=getBounds();
		g.clearRect(r.x, r.y, r.width, r.height);
		Dimension center=getSize();
		center.setSize(center.width/2,center.height/2);
		int radius=Math.round(Math.min(center.width,center.height)*0.85f);
		g.setColor(Color.orange);
		int x1=center.width;
		int y1=center.height-radius;
		int x2=center.width+radius;
		int y2=center.height;
		int x3=center.width;
		int y3=center.height+radius;
		int x4=center.width-radius;
		int y4=center.height;
		xp[1]=x1;yp[1]=y1;
		xp[2]=x2;yp[2]=y2;
		xp[3]=x3;yp[3]=y3;
		xp[4]=x4;yp[4]=y4;
		xp[0]=x1;yp[0]=y1;
		g.drawLine(
				center.width, 
				center.height-radius, 
				center.width,
				center.height+radius);
		g.drawLine(
				center.width-radius, 
				center.height, 
				center.width+radius,
				center.height);
		g.drawPolygon(xp, yp, 5);
		int radius2=(int)Math.floor(radius/2.0f);
		x1=center.width+radius2;
		y1=center.height-radius2;
		x2=center.width+radius2;
		y2=center.height+radius2;
		x3=center.width-radius2;
		y3=center.height+radius2;
		x4=center.width-radius2;
		y4=center.height-radius2;
		xp[1]=x1;yp[1]=y1;
		xp[2]=x2;yp[2]=y2;
		xp[3]=x3;yp[3]=y3;
		xp[4]=x4;yp[4]=y4;
		xp[0]=x1;yp[0]=y1;
		g.drawPolygon(xp, yp, 5);
	}
}

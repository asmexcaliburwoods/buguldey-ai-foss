package corewars.impl;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import corewars.Situation;
import corewars.SituationVisualiser;
import corewars.Subject;

public class TriangleSituationVisualiserImpl_Swing extends JPanel implements SituationVisualiser {
	private static final double PI_MUL_2_DIV_3 = 2*Math.PI/3;
	private static final long serialVersionUID = -547231691868945192L;
	private TriangleSituationImpl situation;
	private TriangleSituationVisualiserImpl_Logger loggervis;
	@SuppressWarnings("unused")
	private final WorldVisualiser_Swing main;
	private float angle;

	TriangleSituationVisualiserImpl_Swing(WorldVisualiser_Swing mainvis){
		loggervis=new TriangleSituationVisualiserImpl_Logger(mainvis.getLogger());
		main=mainvis;
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
//		setIgnoreRepaint(false);
	}
	
	public boolean isLightweight(){return true;}
	public boolean isOpaque(){return false;}
	
	@Override
	public void eternityEnd() {
		loggervis.eternityEnd();
	}

	@Override
	public void eternityStart() {
		loggervis.eternityStart();
	}

	@Override
	public void set(Situation situation) {
		this.situation=(TriangleSituationImpl)situation;
		setToolTipText(getSubject().getLookerDescription(null));
		loggervis.set(situation);
		this.situation.getPropertyChangeSupport().addPropertyChangeListener(
				TriangleSituationImpl.PROPERTY_NAME_ANGLE, new PropertyChangeListener(){
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						float angle=(Float)evt.getNewValue();
						draw(angle);
					}});
	}
	protected void draw(float angle) {
		this.angle=angle;repaint();
	}
	
	public Subject getSubject() {
		return situation.getSubject();
	}
	public Component getMainWidget() {
		return this;
	}
	private final int[] xp=new int[4];
	private final int[] yp=new int[4];
//	public void update(Graphics g){
//		paint(g);
//	}
//	public void paintAll(Graphics g){
//		paint(g);
//	}
	public void paint(Graphics g){
		Rectangle r=getBounds();
		g.clearRect(r.x, r.y, r.width, r.height);
		float angle=this.angle;
		Dimension center=getSize();
		center.setSize(center.width/2,center.height/2);
		float radius=Math.min(center.width,center.height)*0.85f;
		g.setColor(Color.red);
		int x1=center.width+(int)Math.round(radius*Math.cos(angle));
		int y1=center.height+(int)Math.round(radius*Math.sin(angle));
		int x2=center.width+(int)Math.round(radius*Math.cos(angle+PI_MUL_2_DIV_3));
		int y2=center.height+(int)Math.round(radius*Math.sin(angle+PI_MUL_2_DIV_3));
		int x3=center.width+(int)Math.round(radius*Math.cos(angle-PI_MUL_2_DIV_3));
		int y3=center.height+(int)Math.round(radius*Math.sin(angle-PI_MUL_2_DIV_3));
		xp[1]=x1;yp[1]=y1;
		xp[2]=x2;yp[2]=y2;
		xp[3]=x3;yp[3]=y3;
		xp[0]=x1;yp[0]=y1;
		g.drawLine(
				center.width, 
				center.height, 
				x1,
				y1);
		g.drawLine(
				center.width, 
				center.height, 
				x2,
				y2);
		g.drawLine(
				center.width, 
				center.height, 
				x3,
				y3);
		g.drawPolygon(xp, yp, 4);
	}
}

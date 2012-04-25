package corewars.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import corewars.HomoSapiensSapiensInterface;

public class WorldVisualiser_Swing extends JFrame {
	public final Image ICON=new ImageIcon(getClass().getResource("/christianity/SOPHIA_ICON.jpg")).getImage();
	private static final long serialVersionUID = 4331443014964646381L;
	private List<TriangleSituationVisualiserImpl_Swing> situvisualisers=new ArrayList<TriangleSituationVisualiserImpl_Swing>();
	private JTextField logEntry=new JTextField("");
	private JTextPane logPane=new JTextPane();
	{
		logPane.setText("");
	}
	private JPanel godPane=new JPanel(new BorderLayout());
	{
		godPane.setSize(300,300);
	}
	private JPanel creaturesPane=new JPanel(new BorderLayout());
	{
		creaturesPane.setSize(200,200);
	}
	private JPanel subjectsPane=new JPanel(null);
	{
		subjectsPane.add(godPane);
		subjectsPane.add(creaturesPane);
		repositionPanesOnFrameResize();
	}
	private final class ImagePanel extends JPanel {
		private final static long serialVersionUID = -8493694850098294024L;
		private ImagePanel(LayoutManager layout) {
			super(layout);
		}
		public Dimension getPreferredSize(){
			return new Dimension(ICON.getWidth(ImagePanel.this),ICON.getHeight(ImagePanel.this));
		}		
		public void update(Graphics g){
			paint(g);
		}
		public void paint(Graphics g){
			g.drawImage(ICON,0,0,ImagePanel.this);
		}
	}
	private static abstract class Event{
		public abstract String asString();
	}
	public static class MousWheelRotationEvent extends Event{

		private final int steps;

		public MousWheelRotationEvent(int steps) {
			this.steps = steps;
		}
		
		public static final String PREFIX="MOUSE WHEEL ROTATION ";
		
		@Override
		public String asString() {
			return PREFIX+steps;
		}
	}
	public static class WordEvent extends Event{

		private final String in;

		public WordEvent(String in) {
			this.in = in;
		}
		
		@Override
		public String asString() {
			return in;
		}
	}
	private static final int MAX_LOG_LENGTH=1024;
	private class LoggerImpl implements Logger{
		private CoreWarsImpl corewars;

		private LoggerImpl(CoreWarsImpl corewars){
			this.corewars=corewars;
		}
		@Override
		public boolean areAnglesLogged(){return false;}
		
		@Override
		public synchronized void log(String s){
			if(s==null)s="NULL";
			int len=logPane.getDocument().getLength();
			try {
				while(len>MAX_LOG_LENGTH){
					logPane.getDocument().remove(0, MAX_LOG_LENGTH/2);
					len=logPane.getDocument().getLength();
				}
				logPane.getDocument().insertString(len,len>0?"\n"+s:s,null);
				len=logPane.getDocument().getLength();
				logPane.setCaretPosition(len);
			} catch (BadLocationException e) {
				//throw new RuntimeException(e);
			}	
		}

		@Override
		public void pause() {corewars.pause();}

		@Override
		public void resume() {corewars.resume();}

		@Override
		public void sleep() {corewars.sleep();}
		@Override
		public String input() {
			while(true){
				Event ev=queue.poll();
				if(ev!=null){
					String s=ev.asString();
					log("WORD of Homo sapiens sapiens: "+s);
					return s;
				}
				synchronized (WorldVisualiser_Swing.this) {
					try{WorldVisualiser_Swing.this.wait(33);}catch(InterruptedException e){throw new RuntimeException(e);}
				}
				continue;
			}
		}
		@Override
		public void enqueueMouseWheelRotation(int steps) {
			enqueueEvent(new MousWheelRotationEvent(steps));			
		}
		private ConcurrentLinkedQueue<Event> queue=new ConcurrentLinkedQueue<Event>();
		private void enqueueEvent(Event e) {
			if(!queue.add(e))
				throw new AssertionError("queue.add invalid behavior for: "+e.asString());
		}
		@Override
		public void enqueueWord(String in) {
			enqueueEvent(new WordEvent(in));
		}
	}
	private Logger logger;

	WorldVisualiser_Swing(CoreWarsImpl corewars){
		super(corewars.getMultiverseName());
		logger=new LoggerImpl(corewars);
	
	    setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
		    	getDefaultScreenDevice().getDefaultConfiguration().getBounds());
      
   		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		subjectsPane.addComponentListener(new ComponentListener(){

			@Override
			public void componentHidden(ComponentEvent e) {
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				
			}

			@Override
			public void componentResized(ComponentEvent e) {
				repositionPanesOnFrameResize();
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				
			}});
		addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
				logger.resume();
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				logger.sleep();
				System.exit(0);
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				logger.pause();
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				logger.resume();
				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				logger.pause();				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}});
		setState(Frame.NORMAL);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		getContentPane().setLayout(new BorderLayout());
		JLabel top = new JLabel(corewars.getIncompleteDescription(),JLabel.CENTER);
//		top.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		getContentPane().add(top,"North");
		JPanel left=new JPanel(new BorderLayout());
		JPanel ABRAXAS=new ABRAXASSymbol(){
			private static final long serialVersionUID = 9074138548285739409L;

			public Dimension getPreferredSize(){return new Dimension(100,100);}
		};
		JPanel icon=new ImagePanel(null);
		icon.setToolTipText("SOPHIA ICON");
		left.add(icon,"Center");
		left.add(ABRAXAS,"North");
		getContentPane().add(left,"West");
		JPanel right=new JPanel(new BorderLayout()){
			private static final long serialVersionUID = -9181465823766277253L;
			public Dimension getPreferredSize(){return new Dimension(500,100);}
		};		
		logPane.setEditable(false);
		Component logScrollPane = logPane;//new JScrollPane(logPane);
		right.add(logScrollPane,"Center");
		JPanel entryPane=new JPanel(new BorderLayout());
		entryPane.add(new JLabel("WORD of Homo sapiens sapiens: "),"West");
		entryPane.add(logEntry,"Center");
		right.add(entryPane,"South");
		getContentPane().add(subjectsPane,BorderLayout.CENTER);
		getContentPane().add(right,BorderLayout.EAST);
		logEntry.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String in=logEntry.getText();
				logEntry.setText("");
				logger.enqueueWord(in);
				synchronized (WorldVisualiser_Swing.this) {
					WorldVisualiser_Swing.this.notify();
				}
			}
		});
		addMouseWheelListener(new MouseWheelListener(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				logger.enqueueMouseWheelRotation(e.getWheelRotation());
				synchronized (WorldVisualiser_Swing.this) {
					WorldVisualiser_Swing.this.notify();
				}
			}});
		setVisible(true);
//				logScrollPane.setIgnoreRepaint(false);
//				logPane.setIgnoreRepaint(false);
//				setIgnoreRepaint(false);
//				for(TriangleSituationVisualiserImpl_Swing sv:situvisualisers){
//					sv.setIgnoreRepaint(false);
//				}
	}
	private void repositionPanesOnFrameResize() {
		Dimension d=subjectsPane.getSize();
		d.setSize(d.width/2,d.height/2);
		//d is now a center
		godPane.setLocation(d.width-godPane.getWidth()/2, 0);
		creaturesPane.setLocation(d.width-creaturesPane.getWidth()/2, godPane.getHeight());
	}
	public Logger getLogger() {
		return logger;
	}
	public void add(TriangleSituationVisualiserImpl_Swing situvis){
		situvisualisers.add(situvis);
		JPanel container; 
		if(situvis.getSubject() instanceof HomoSapiensSapiensInterface)container=godPane;
		else container=creaturesPane;
		container.add(situvis.getMainWidget(),BorderLayout.CENTER);
	}
}

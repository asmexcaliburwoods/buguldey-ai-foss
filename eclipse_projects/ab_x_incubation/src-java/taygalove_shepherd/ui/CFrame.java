package taygalove_shepherd.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.Registry;
import taygalove_shepherd.i18n.m.M;
import taygalove_shepherd.sachok.PersistentState;
import taygalove_shepherd.tostring.ToString;
import taygalove_shepherd.util.ScreenBoundsUtil;
import taygalove_shepherd.util.StringUtil;

public class CFrame extends JFrame{
    private static Image windowIcon;
    static{
    	URL logourl=CFrame.class.getResource("/taygalove_shepherd/ab-logo.jpg");
    	windowIcon = new ImageIcon(logourl).getImage();
    }
    private String preferencesKey;
    
    /** If preferencesKey is null, bounds and extended state for this frame are not
     *  stored  in preferences */
    public CFrame(NamedCaller nc, String title, String preferencesKey){
        super(M.format(M.SILENTLY_SAYS1, new String[]{
				ToString.callerToNameOfCaller(nc), 
				title==null?"null":title}));
        this.preferencesKey=preferencesKey;
        initFrame(nc);
    }
    public void setVisible(boolean b){
        super.setVisible(b);
        if(b) Registry.objectShown(this);
    }
    public void show(){
        super.show();
        Registry.objectShown(this);
    }
    public void dispose(){
        //worakround for bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4289940
        //java.lang.IllegalStateException: Can't dispose InputContext while it's active
        Component cmp[]=getComponents();
        if(cmp!=null)
            for(int i=0;i<cmp.length;i++){
                Component c=cmp[i];
                if(c!=null)c.removeNotify();
            }
//    Component cmp[]=getComponents();
//    if(cmp!=null)
//      for(int i=0;i<cmp.length;i++){
//        Component c=cmp[i];
//        if(c!=null)getInputContext().removeNotify(c);
//      }
//    getInputContext().removeNotify(this);
//    removeNotify();
/*
ere's what worked for me:

    // this avoids an obscure Java 1.3 and above AWT bug involving
    // InputContext.dispose():
    for (int i=0; i < myPanel.getComponentCount(); ++i)
      myPanel.getComponent(i).removeNotify();
    myPanel.removeAll();

In short, I call removeNotify() on all the components in a
container before I call removeAll() on that container.  This
should be taken care of by the call to removeAll() itself,
but it isn't always.  Of course, as mentioned elsewhere,
make sure none of your components override removeNotify()
without calling super.removeNotify().  Hope this helps.
*/
        //end of workaround
        super.dispose();
        Registry.objectDisposed(this);
    }
    private void initFrame(final NamedCaller nc){
        setIconImage(windowIcon);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                if(storedInPreferences())
                	PersistentState.getInstance().saveFrame(nc, CFrame.this);
            }
        });
        Rectangle screenBounds= ScreenBoundsUtil.getScreenBounds();
        Dimension d=getPreferredSize();
        int width=d.width;
        int height=d.height;
        int x=(int)(screenBounds.x+Math.random()*(screenBounds.width-width-1));
        int y=(int)(screenBounds.y+Math.random()*(screenBounds.height-height-1));

        if(storedInPreferences())
            taygalove_shepherd.sachok.PersistentState.getInstance().loadFrame(
                    CFrame.this,new Rectangle(x,y,width,height),getDefaultExtendedState());
        else{
            setBounds(x,y,width,height);
            setExtendedState(getDefaultExtendedState());
        }
    }

    protected int getDefaultExtendedState() {
        return Frame.NORMAL;
    }

    public String getPreferencesKey(){return preferencesKey;}
    public boolean storedInPreferences(){return preferencesKey!=null;}
}

package taygalove_shepherd.sachok;


import java.awt.Frame;
import java.awt.Rectangle;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.ui.CFrame;
import taygalove_shepherd.util.ExceptionUtil;

public class PersistentState {
    private static final String PACKAGE="";
    private PreferencesForABProject prefsUser= PreferencesForABProject.userNodeForABProject.node(PACKAGE);
//  private Preferences prefsSystem=Preferences.systemNode.node(PACKAGE);
    private static PersistentState instance=new PersistentState();
    public static PersistentState getInstance(){
        return instance;
    }
    public synchronized void saveFrame(NamedCaller nc, CFrame f){
        PreferencesForABProject node=prefsUser.node("frame\\"+f.getPreferencesKey());
        Rectangle bounds=f.getBounds();
        int extendedState=f.getExtendedState();
        try{
            node.putInt(nc, "x",bounds.x);
            node.putInt(nc, "y",bounds.y);
            node.putInt(nc, "w",bounds.width);
            node.putInt(nc, "h",bounds.height);
            node.putInt(nc, "state",extendedState&~Frame.ICONIFIED);
        } catch(Exception e){
        	ExceptionUtil.handleException(nc, e);
        }
    }
    public synchronized void loadFrame(
            taygalove_shepherd.ui.CFrame f,Rectangle defaultBounds,int defaultExtendedState){
        PreferencesForABProject node=prefsUser.node("frame\\"+f.getPreferencesKey());
        f.setBounds(
                node.getInt("x",defaultBounds.x),
                node.getInt("y",defaultBounds.y),
                node.getInt("w",defaultBounds.width),
                node.getInt("h",defaultBounds.height)
        );
        f.setExtendedState(
                node.getInt("state",defaultExtendedState));
    }
//    public void saveAccountData(String accountName,String password){
//        try{
//            Preferences node=prefsUser;
//            node.putString("account",accountName);
//            node.putString("p",password);
//        } catch(Exception e){
//            ExceptionUtil.handleException(e);
//        }
//    }
//    public String loadAccountName(String default_){
//        return prefsUser.getString("account",default_);
//    }
//    public String loadAccountPassword(String default_){
//        return prefsUser.getString("p",default_);
//    }
}

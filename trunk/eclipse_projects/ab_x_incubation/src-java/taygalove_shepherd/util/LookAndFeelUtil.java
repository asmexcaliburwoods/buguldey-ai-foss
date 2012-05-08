package taygalove_shepherd.util;

import javax.swing.*;

public class LookAndFeelUtil {
    public static void initLAF(){
        try{
            //-Dswing.laf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
        	//if(OSVersionUtil.isMelkosoftOkna())
        	//	UIManager.setLookAndFeel(com.sun.java.swing.plaf.windows.WindowsLookAndFeel.class.getName());
//            CustomWindowsTreeUI.install(); //todo
//            CustomBasicListUI.install();   //todo
//            CustomBasicTableUI.install();  //todo
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

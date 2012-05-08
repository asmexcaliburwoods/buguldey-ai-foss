package taygalove_shepherd.addressbook.ab.ui.pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Keymap;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.addressbook.ab.datamodel.Human;
import taygalove_shepherd.ui.CFrame;
import taygalove_shepherd.ui.CTextArea;
import taygalove_shepherd.util.ExceptionUtil;
import taygalove_shepherd.util.MsgBoxUtil;
import taygalove_shepherd.util.ScreenBoundsUtil;


public class HumanPane extends JPanel {
	private static final long serialVersionUID = -6765886254219068245L;
	private JTextField updated;
    private taygalove_shepherd.addressbook.ab.datamodel.Human human;
    @SuppressWarnings("unchecked")
    private static final Class<Object>[] CLASS_0_ARRAY = new Class[0];
    private static final Object[] OBJECT_0_ARRAY = new Object[0];
    @SuppressWarnings("unchecked")
    private static final Class<String>[] SIGNATURE_STRING = new Class[]{String.class};
    private java.util.List<JComponent> tabOrder=new LinkedList<JComponent>();

    public static interface ActionListener{
        /** @return true to close this frame */
        boolean addPressed(
        		Human human,CFrame f);
        /** @return true to close this frame */
        boolean cancelPressed();
    }

    /**
    *
    * @param h
    * @param actionListener may be null---in this case buttons
    *        Add and Cancel will not be present
    */
    public HumanPane(NamedCaller nc, Human h, final ActionListener actionListener, final CFrame frame, JComponent nextInTabOrder){
    	this(nc, h,actionListener,frame);
    	tabOrder.add(nextInTabOrder);
    }
    /**
     *
     * @param h
     * @param actionListener may be null---in this case buttons
     *        Add and Cancel will not be present
     */
    public HumanPane(final NamedCaller nc, Human h, final ActionListener actionListener, final CFrame frame) {
        super(new BorderLayout());
        this.human=h;
        JPanel pane;
        add(BorderLayout.CENTER,new JScrollPane(pane=new JPanel(new GridLayout(1,2))));
        if(actionListener!=null){
            JPanel buttonsPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton buttonAdd = new JButton(new AbstractAction("Save") {
				private static final long serialVersionUID = -246291055791915908L;

				public void actionPerformed(ActionEvent e) {
                    try {
                        if(actionListener.addPressed(human,frame))frame.dispose();
                    } catch (Exception e1) {
                        ExceptionUtil.handleException(nc, e1);
                    }
                }
            });
            buttonAdd.setMnemonic('S');
            buttonsPane.add(buttonAdd);
            JButton buttonCancel = new JButton(new AbstractAction("Cancel") {
				private static final long serialVersionUID = 7300224261968961057L;

				public void actionPerformed(ActionEvent e) {
                    try {
                        if(actionListener.cancelPressed())frame.dispose();
                    } catch (Exception e1) {
                        taygalove_shepherd.util.ExceptionUtil.handleException(nc, e1);
                    }
                }
            });
            buttonCancel.setMnemonic('C');
            buttonsPane.add(buttonCancel);
            add(BorderLayout.SOUTH, buttonsPane);
        }
        JPanel leftPane=new JPanel(new BorderLayout());
        JPanel ritePane =new JPanel(new BorderLayout());
        pane.add(leftPane);
        pane.add(ritePane);
        Box leftBox=new Box(BoxLayout.Y_AXIS);
        Box riteBox=new Box(BoxLayout.Y_AXIS);
        leftPane.add(leftBox, BorderLayout.NORTH);
        ritePane.add(riteBox, BorderLayout.NORTH);
        leftBox.add(createLabel("First name (ASCII):"));
        leftBox.add(createTextField("FirstNameAscii"));

        leftBox.add(createLabel("Last name (ASCII):"));
        leftBox.add(createTextField("LastNameAscii"));

        leftBox.add(createLabel("Middle name (ASCII):"));
        leftBox.add(createTextField("MiddleNameAscii"));

        leftBox.add(createLabel("First name (natĭve):"));
        leftBox.add(createTextField("FirstNameNative"));

        leftBox.add(createLabel("Last name (natĭve):"));
        leftBox.add(createTextField("LastNameNative"));

        leftBox.add(createLabel("Middle name (natĭve):"));
        leftBox.add(createTextField("MiddleNameNative"));

        leftBox.add(createLabel("Nicks:"));
        leftBox.add(createTextField("Nicks"));

        leftBox.add(createLabel("Companies and titles (one company per line):"));
        leftBox.add(createTextArea(nc, "CompaniesAndTitles"));

        leftBox.add(createLabel("E-mails (one per line):"));
        leftBox.add(createTextArea(nc, "Emails"));

        leftBox.add(createLabel("Web addresses (one per line):"));
        leftBox.add(createTextArea(nc, "WebUrls"));

        leftBox.add(createLabel("Blogs (one per line):"));
        leftBox.add(createTextArea(nc, "Blogs"));

        leftBox.add(createLabel("Street address:"));
        leftBox.add(createTextArea(nc, "StreetAddress",10));

        leftBox.add(createLabel("Updated:"));
        leftBox.add(updated=new JTextField(""));
        updated.setEditable(false);
        updated();

        //***

        riteBox.add(createLabel("Home #s:"));
        riteBox.add(createTextField("HomePhones"));

        riteBox.add(createLabel("Cell #s:"));
        riteBox.add(createTextField("CellPhones"));

        riteBox.add(createLabel("Office #s:"));
        riteBox.add(createTextField("OfficePhones"));

        riteBox.add(createLabel("ICQ #s:"));
        riteBox.add(createTextField("IcqNumbers"));

        riteBox.add(createLabel("Jabber/Google Talk IDs:"));
        riteBox.add(createTextField("JabberIds"));

        riteBox.add(createLabel("Skype Logins:"));
        riteBox.add(createTextField("SkypeLogins"));

        riteBox.add(createLabel("MSN Logins:"));
        riteBox.add(createTextField("MsnLogins"));

        riteBox.add(createLabel("Notes:"));
        riteBox.add(createTextArea(nc, "Notes",24));
    }

    private static JComponent createLabel(String text){
    	JLabel label=new JLabel(text);
    	JPanel p=new JPanel(new BorderLayout());
    	p.add(label,BorderLayout.WEST);
    	return p;
    }
    private void updated() {
        updated.setText(new SimpleDateFormat(taygalove_shepherd.i18n.m.M.JAVA_DATE_FORMAT).format(human.getLastUpdated()));
    }

    public static void main(String[] args){
    	NamedCaller nc=new NamedCaller() {
			@Override
			public String name() {
				return "HumanPane.Test";
			}
		};
        taygalove_shepherd.util.LookAndFeelUtil.initLAF();
        final ActionListener actionListener = new ActionListener() {
            public boolean addPressed(taygalove_shepherd.addressbook.ab.datamodel.Human human,
            		taygalove_shepherd.ui.CFrame f) {
                System.exit(0);
                return false;
            }

            public boolean cancelPressed() {
                System.exit(0);
                return false;
            }
        };
        String title = "Test";
        String preferencesKey = "TestHumanPane";
        showNewHumanFrame(nc, title, preferencesKey, actionListener);
    }

    public static void showNewHumanFrame(NamedCaller nc, String title, String preferencesKey, final ActionListener actionListener) {
        final CFrame f=new CFrame(nc, title,preferencesKey){
			private static final long serialVersionUID = -6994485943614587439L;

			public Dimension getPreferredSize() {
                return ScreenBoundsUtil.getScreenBounds().getSize();
            }

            protected int getDefaultExtendedState() {
                return Frame.MAXIMIZED_BOTH;
            }
        };
        HumanPane c=new HumanPane(nc, taygalove_shepherd.addressbook.ab.datamodel.HumanImpl.createNewUnlinkedHuman(), actionListener, f);

        WindowAdapter windowListener = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(actionListener.cancelPressed())f.dispose();
            }
        };
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(c);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.addWindowListener(windowListener);
        f.setVisible(true);
    }
    private JTextField createTextField(final String humanField){
        final JTextField field=new JTextField(getStringViaReflection(humanField));
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                setStringViaReflection(humanField,field.getText());
            }

            public void insertUpdate(DocumentEvent e) {
                setStringViaReflection(humanField,field.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                setStringViaReflection(humanField,field.getText());
            }
        });
        tabOrder.add(field);
        return field;
    }

    private CTextArea createTextArea(NamedCaller nc, final String humanField){
        return createTextArea(nc, humanField,2);
    }
    private void handleTab(final NamedCaller nc, boolean reverse, Object source){
		int i=tabOrder.indexOf(source);
		if(i<0){MsgBoxUtil.showError(nc, "handleTab");return;}
		if(!reverse&&i==tabOrder.size()-1)i=0;
		else
			if(reverse&&i==0)i=tabOrder.size()-1;
			else
				if(reverse)--i;
				else ++i;
		JComponent c=tabOrder.get(i);
		c.requestFocusInWindow();
    }
    private CTextArea createTextArea(final NamedCaller nc, final String humanField,int rows){
        final CTextArea field=
        	new CTextArea(getStringViaReflection(humanField),rows,0);
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                setStringViaReflection(humanField,field.getText());
            }

            public void insertUpdate(DocumentEvent e) {
                setStringViaReflection(humanField,field.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                setStringViaReflection(humanField,field.getText());
            }
        });
		Keymap keymap=taygalove_shepherd.ui.CTextArea.addKeymap("keymap"+tabOrder.size(), field.getKeymap());
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0), 
				new AbstractAction(){
					private static final long serialVersionUID = -1354646991611133126L;

					@Override
					public void actionPerformed(ActionEvent e) {
						handleTab(nc, false, field);
					}});
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,InputEvent.SHIFT_MASK), 
				new AbstractAction(){
					private static final long serialVersionUID = -110179313042841593L;

					@Override
					public void actionPerformed(ActionEvent e) {
						handleTab(nc, true, field);
					}});
        field.setKeymap(keymap);
        tabOrder.add(field);
        return field;
    }

    private void setStringViaReflection(String humanField,String value) {
        final taygalove_shepherd.addressbook.ab.datamodel.Human human = this.human;
        setStringViaReflection(human, humanField, value);
        Date lastUpdated = new Date();
        human.setLastUpdated(lastUpdated);
        updated();
    }

    public static void setStringViaReflection(taygalove_shepherd.addressbook.ab.datamodel.Human human, String humanField, String value) {
        try {
            human.getClass().getMethod("set"+humanField,SIGNATURE_STRING).invoke(human,new Object[]{value});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStringViaReflection(String humanField) {
        final taygalove_shepherd.addressbook.ab.datamodel.Human human = this.human;
        return getStringViaReflection(human, humanField);
    }

    public static String getStringViaReflection(taygalove_shepherd.addressbook.ab.datamodel.Human human, String humanField) {
        try {
            return (String) human.getClass().getMethod("get"+humanField,CLASS_0_ARRAY).invoke(human,OBJECT_0_ARRAY);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}

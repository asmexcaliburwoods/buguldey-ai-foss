package com.jamud.connections;

import jamud.Jamud;
import jamud.object.Player;
import jamud.object.AbstractConnection;
import jamud.plugin.*;
import jamud.util.*;

import org.jcq2k.*;
import org.jcq2k.icq2k.*;

import java.util.*;

public class IcqAdapterServer extends JamudPlugin 
{
    private final static String ICQ_ADAPTER_VERSION = "1.0.0005";
    
    private final static org.log4j.Category CAT = org.log4j.Category.getInstance(IcqAdapterServer.class.getName());
  
    private static final String PARAM_ICQ_UIN = "ICQ_UIN";
    private static final String PARAM_ICQ_PASSWORD = "ICQ_PASSWORD";
    private static final String PARAM_AD_THE_AD = "ADVERTISE_THE_AD_COMMAND";

    private static final MessagingNetwork mn = new ICQ2KMessagingNetwork();

    public final String getName() {
        return "Icq Adapter Server";
    }

    public final static String static_getVersion() {
        return ICQ_ADAPTER_VERSION; // + "; jcq2k version: "+ICQ2KMessagingNetwork.getVersion();
    }
    
    public final String getVersion() {
        return static_getVersion();
    }
    
    static 
    {
        log("ICQ ADAPTER VERSION: "+static_getVersion());
    }
    
    public final String getAuthor() {
        return "huthut <joxy@sf.net>";
    }

    public final String getInfo() {
        return "This is the Icq Adapter Server.";
    }

    private String adapterIcqUin;
    private String adapterIcqPassword;
    private boolean adTheAd;

    private MessagingNetworkListener l = new MessagingNetworkListener()
    {
        public void messageReceived(byte networkId, String srcLoginId, String dstLoginId, String text)
        {
            try
            {
                if ((!srcLoginId.equals("0")) && dstLoginId.equals(adapterIcqUin))
                {
                    handleIncomingMessage(srcLoginId, text);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }            

        public void contactsReceived(byte networkId, String srcLoginId, String dstLoginId, String[] contactsLoginIds, String[] contactsNicks)
        {
        }

        public void statusChanged(byte networkId, final String srcLoginId, final String dstLoginId, int status, final int reasonCategory, String reasonMessage)
        {
            try
            {
                if (status == MessagingNetwork.STATUS_OFFLINE && srcLoginId.equals(adapterIcqUin)) 
                {
                    new Thread("handleLogoff")
                    {
                        public void run()
                        {
                            if (srcLoginId.equals(dstLoginId))
                                handleAdapterLogoff(reasonCategory);
                            else
                                handleLogoff(dstLoginId);
                        }
                    }.start();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    };
        
    private int state = STATE_TERMINATED;

    public int initializableState() {
        return this.state;
    }

    public IcqAdapterServer()
    {
    }

    private Map playerUin2connection = new HashMap();
    
    private IcqConnection getIcqConnection(String playerUin)
    {
        return (IcqConnection) playerUin2connection.get(playerUin);        
    }
    
    private void addIcqConnection(IcqConnection c)
    {
        playerUin2connection.put(c.getPlayerUin(), c);        
    }
    
    private void killPlayer(IcqConnection playerConnection)   
    {
            killPlayer0(playerConnection, true);
    }
    
    private void killPlayer0(IcqConnection playerConnection, boolean removeMapping)
    {
        Player p;
        synchronized (playerUin2connection)
        {
            p = playerConnection.getPlayer();
            if (removeMapping) 
                playerUin2connection.remove(playerConnection.getPlayerUin());
        }
        if(p != null) p.terminate();
    }
    
    private void killPlayers()
    {
        ArrayList players;
        synchronized (playerUin2connection)
        {
            players = new ArrayList(playerUin2connection.size());
            
            Iterator it = playerUin2connection.values().iterator();
            while (it.hasNext())
            {
                IcqConnection c = (IcqConnection) it.next();
                Player p = c.getPlayer();
                if(p != null) 
                {
                    players.add(p);
                }
            }
            playerUin2connection.clear();
        }
        Iterator it = players.iterator();
        while (it.hasNext())
        {
            Player p = (Player) it.next();
            p.terminate();
        }
    }
    
    private static void log(String m)
    {
      
        CAT.debug(m);
        //System.out.println("icq/"+adapterIcqUin+": "+m);
    }
    
    private static void log(String m, Throwable tr)
    {
        CAT.debug(m, tr);
        //log(m);
        //log(tr);
    }
    
    private static void log(Throwable tr)
    {
        CAT.debug("", tr);
        //tr.printStackTrace();
    }
    
    private void handleLogoff(String dstLoginId)
    {
        synchronized (playerUin2connection)
        {
            killPlayer0(getIcqConnection(dstLoginId), true);
        }
        
        try
        {
            mn.removeFromContactList(adapterIcqUin, dstLoginId);
        }
        catch (Exception ex)
        {
        }
    }

    private void handleIncomingMessage(String senderUin, String text)
    {
        if (senderUin.equals(adapterIcqUin)) return;
        
        IcqConnection c = null;
        boolean isnew = false;

        synchronized (playerUin2connection)
        {
            c = getIcqConnection(senderUin);
            if (c == null)
            {
                c = new IcqConnection(senderUin);
                addIcqConnection(c);
                isnew = true;
            }
        }
        if (isnew)
        {
            try
            {
                mn.sendMessage(adapterIcqUin, senderUin, "Connection created."+(adTheAd ? "\n\nUse ``ad icq-number'' command to send the Jamud intro screen to your friends." : ""));
            }
            catch (Exception ex)
            {
            }
            new Player(c);
            c.start();
        }
        else
        {
            StringTokenizer st = new StringTokenizer(text, "\n", false);
            while (st.hasMoreTokens())
            {
                String cmd = st.nextToken().trim();
                if (cmd.length() > 0) 
                {
                    Player p = c.getPlayer();
                    if (p != null)
                    {
                        executeCommand(p, cmd);
                    }
                    else 
                    {
                        killPlayer(c);
                        return;
                    }
                }
            }
        }
    }
    
    
    private void executeCommand(Player p, String cmd)
    {
        StringTokenizer st = new StringTokenizer(cmd);
        String firstTok = null;
        String secondTok = null;
        if (st.hasMoreTokens()) firstTok = st.nextToken();
        if (st.hasMoreTokens()) secondTok = st.nextToken();
        if (firstTok.equalsIgnoreCase("ad"))
        {
            try
            {
                Integer.parseInt(secondTok);
                handleIncomingMessage(secondTok, "");
                return;
            }
            catch (Exception ex)
            {
            }
        }
        p.enqueueCommand(cmd);
    }

    private void handleAdapterLogoff(int logoffCategory)
    {
        if (logoffCategory == MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_PLUGIN_LOGOUT_CALLER) 
            return;
        log("handleAdapterLogoff, cat="+logoffCategory, new Exception("dumpstack"));
            
        scheduleLogin();
    }
    
    private Thread reloginThread = null;
    
    private synchronized void scheduleLogin()
    {
        Thread reloginThread = this.reloginThread;
        if (reloginThread != null) return;
        
        reloginThread = new Thread("icq login")
        {
            public void run()
            {
                System.out.println(this+" started");
                int attempts = 0;
                int category = MessagingNetworkException.CATEGORY_NOT_CATEGORIZED;
                
                try
                {
                    for (;;)
                    {
                        if (interrupted()) return;
                        
                        int sleepTimeMillis;
                        switch (category)
                        {
                            case MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_PLUGIN_LOGOUT_CALLER:
                                return;
                            case MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR:
                                sleepTimeMillis = 30 * 60 * 1000; //30 minutes
                                break;
                            default:
                                if (attempts == 0) sleepTimeMillis = 0;
                                else if (attempts < 2) sleepTimeMillis = 2000;
                                else 
                                {   if (attempts == 2) killPlayers();
                                    if (attempts < 5) sleepTimeMillis = 5000;
                                    else sleepTimeMillis = 5 * 60 * 1000; //5 minutes
                                }
                                break;
                        }
                        
                        ++attempts;
                        if (attempts > 1000000) attempts = 1000000;
                        
                        if (sleepTimeMillis > 0)
                        {
                            System.out.println(this+": sleeping "+(sleepTimeMillis/1000)+" seconds before adapter login attempt #"+attempts);
                            sleep(sleepTimeMillis);
                        }
                        
                        try
                        {
                            String uin = adapterIcqUin;
                            if (uin == null) { System.err.println("adapterIcqUin is null, terminating"); terminate(); return; }
                            String pass = adapterIcqPassword;
                            if (pass == null) { System.err.println("adapterIcqPassword is null, terminating"); terminate(); return; }
                            mn.login(uin, pass, null, MessagingNetwork.STATUS_ONLINE);
                            return;
                        }
                        catch (MessagingNetworkException ex)
                        {
                            category = ex.getCategory();
                        }
                    }
                }
                catch (InterruptedException iex)
                {
                }
                catch (Throwable tr)
                {
                    tr.printStackTrace();
                }
                finally
                {
                    synchronized(IcqAdapterServer.this)
                    {
                        IcqAdapterServer.this.reloginThread = null;
                    }
                        
                    System.out.println(this+" finished");
                }
            }
        };
        reloginThread.start();
    }

    public synchronized boolean initialize() {
        System.out.println( "begin: IcqAdapterServer.initialize(), version: "+getVersion());
        if(this.state > STATE_TERMINATED) {
            System.out.println( " not yet terminated" );
            System.out.println( "end: IcqAdapterServer.initialize()" );
            return false;
        } else {
            this.state = STATE_INITIALIZING;
        }

        try {       
            adapterIcqUin = parameters().getAttribute( PARAM_ICQ_UIN, "" );
            if (adapterIcqUin == null) throw new NullPointerException(PARAM_ICQ_UIN+" is null");
            if (adapterIcqUin.trim().equals("")) throw new Exception(PARAM_ICQ_UIN+" is invalid: \""+adapterIcqUin+"\"");

            adapterIcqPassword = parameters().getAttribute( PARAM_ICQ_PASSWORD, "" );
            if (adapterIcqPassword == null) throw new NullPointerException(PARAM_ICQ_PASSWORD+" is null");
            if (adapterIcqPassword.equals("")) throw new Exception(PARAM_ICQ_PASSWORD+" is invalid: \"\"");
            
            String adTheAdS = (""+parameters().getAttribute( PARAM_AD_THE_AD, "" )).trim();
            
            if (adTheAdS.equalsIgnoreCase("true")) adTheAd = true;
            else
            if (adTheAdS.equalsIgnoreCase("false")) adTheAd = false;
            else
              throw new Exception(PARAM_AD_THE_AD+" must be either true or false, but it is: \""+adTheAdS+"\"");
            
            mn.init();
            mn.addMessagingNetworkListener(l);
            scheduleLogin();
        } catch(Exception e) {
            e.printStackTrace();

            this.state = STATE_TERMINATED;
            System.out.println( "end: IcqAdapterServer.initialise()" );
            return false;
        }

        this.state = STATE_INITIALIZED;
        System.out.println( "end: IcqAdapterServer.initialise()" );
        return true;
    }


    public synchronized boolean terminate() {
        System.out.println( "begin: IcqAdapterServer.terminate()" );
        if(this.state < STATE_INITIALIZED) {
            System.out.println( " not yet initialized" );
            System.out.println( "end: IcqAdapterServer.terminate()" );
            return false;
        } else {
            this.state = STATE_TERMINATING;
        }

        if (reloginThread != null)
        {
            reloginThread.interrupt();
            reloginThread = null;
        }
        
        try
        {
            mn.removeMessagingNetworkListener(l);
        }
        catch (Exception ex)
        {
        }
        
        mn.deinit();

        this.state = STATE_TERMINATED;
        System.out.println( "end: IcqAdapterServer.terminate()" );
        return true;
    }


    public boolean isActive() {
        return (this.state == STATE_INITIALIZED);
    }
    
    private class IcqConnection extends AbstractConnection
    {
        private final String playerUin;
        
        private IcqConnection(String playerUin)
        {
            this.playerUin = playerUin;
        }
        
        private String getPlayerUin()
        {
            return playerUin;
        }

        private boolean disconnected = false;
                
        public synchronized void disconnect()
        {
            if (disconnected) return;
            disconnected = true;
            
            try
            {
                mn.sendMessage(adapterIcqUin, playerUin, "Connection closed.");
            }
            catch (Exception ex)
            {
            }
            
            log("player.disconnect", new Exception("dumpstack"));
            synchronized (playerUin2connection)
            {
                IcqConnection c = (IcqConnection) playerUin2connection.remove(playerUin);
                if (c != null) killPlayer0(c, false);
            }
        }

        /** delivers the text to icq uin */        
        public void print(String text)
        {
            text = text.trim();
            if (text.length() == 0) return;
            
            byte[] t = text.getBytes();
            try 
            {
                log("player.print");
                Player p = getPlayer();
                if (p == null) { killPlayer(IcqConnection.this); return; }
                
                p.attributes().setAttribute(Player.ATTRIB_PROMPT, "");                

                //if(p.isFlagged( Colour.PLAYER_COLOUR_FLAG )) {
                //    t = AnsiColour.parseColourMarkup( t );
                //} else {
                    t = Colour.wipeColour( t );
                    t = Util.convertCrLf( t );
                //}
            } 
            catch(Exception e) 
            {
                e.printStackTrace();
                return;
            }
                
            try
            {
                mn.sendMessage(adapterIcqUin, playerUin, new String(text));
            } catch(Exception e) {
                //will be handled by handleLogoff delivered by mn
            }
        }

        public void start()
        {
            log("player.start enter");
            //initialize
            new Thread("init player "+playerUin) 
            {
                public void run() 
                {
                    try
                    {
                        mn.addToContactList(adapterIcqUin, playerUin);
                        
                        Player p = getPlayer();
                        if(p != null && !p.initialize()) 
                            p.terminate();
                    }
                    catch(Throwable tr)
                    {
                        tr.printStackTrace();
                        killPlayer(IcqConnection.this);
                    }
                    

                }
            }.start();
            log("player.start leave");
        }
        
        /** Method of identifying what sort of connection this is. */
        public String getType() { return "Icq Connection"; }

        /** Design bug. Should return "ConnectionRemoteSideAddress object". */
        public String getRemoteAddress() { return "0.0.0.0"; }

        /** Design bug. Should return "ConnectionRemoteSideAddress object". */
        public int getRemotePort() { return 0; }
        
        /** Design bug. Should return "ConnectionLocalSideAddress object". */
        public int getLocalPort() { return 0; }
    }
}

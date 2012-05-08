package kernel.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import kernel.environments.icq.ICQPersonalAgentAvatar;
import kernel.logic.TrueFalseAskuser;
import kernel.util.ExceptionUtil;




/**
 * Personal agent acting on behalf of some human person.
 */
public class PersonalAgent extends Cosmos {
	private List<PersonalAgentAvatar> avatars=new LinkedList<PersonalAgentAvatar>();
	public interface PersonalAgentListener{
		void icqAvatarCreated(ICQPersonalAgentAvatar avatar);
	}
	private Set<PersonalAgentListener> listeners=new HashSet<PersonalAgentListener>();
	public interface MessageBase{}
	public MessageBase getMessageBase(){return messageBase;}
	private MessageBaseImpl messageBase=new MessageBaseImpl();
	private class MessageBaseImpl implements MessageBase{}
	private TrueFalseAskuser allowedNetworkingAndInternet=TrueFalseAskuser.Askuser;
	public void allowNetworkingAndInternet(){
		allowedNetworkingAndInternet=TrueFalseAskuser.True;
		//TODO what we should do more than this?
	}
	public void disallowNetworkingAndInternet(){
		allowedNetworkingAndInternet=TrueFalseAskuser.False;
		//TODO what we should do more than this?
	}
	private List<PersonalAgentAvatar> getCloneOfAvatarList(){
		List<PersonalAgentAvatar> avatars;
		synchronized (this.avatars) {
			//clone
			avatars=new ArrayList<PersonalAgentAvatar>(this.avatars);
		}
		return avatars;
	}
	public void makeAllAvatarsBeOnline(){
		List<PersonalAgentAvatar> avatars=getCloneOfAvatarList();
		Iterator<PersonalAgentAvatar> iter=avatars.iterator();
		while(iter.hasNext()){
			PersonalAgentAvatar a=iter.next();
			try{a.makeAvatarBeOnline();}catch(Throwable tr){ExceptionUtil.handleException(tr);}				
		}
	}
	public void makeAllAvatarsBeDisconnected(){
		List<PersonalAgentAvatar> avatars=getCloneOfAvatarList();
		Iterator<PersonalAgentAvatar> iter=avatars.iterator();
		while(iter.hasNext()){
			PersonalAgentAvatar a=iter.next();
			try{a.makeAvatarBeDisconnected();}catch(Throwable tr){ExceptionUtil.handleException(tr);}				
		}
	}
	/**
	 * @see PersonalAgentListener#icqAvatarCreated
	 */
	public ICQPersonalAgentAvatar addICQPersonalAgentAvatar(){
		ICQPersonalAgentAvatar a=new ICQPersonalAgentAvatar();
		synchronized (avatars) {
			avatars.add(a);
		}
		synchronized (listeners) {
			Iterator<PersonalAgentListener> iter=listeners.iterator();
			while(iter.hasNext()){
				PersonalAgentListener listener=iter.next();
				try{listener.icqAvatarCreated(a);}catch(Throwable tr){ExceptionUtil.handleException(tr);}				
			}
		}
		return a;
	};
	//TODO public void addIRCPersonalAgentAvatar(){...;};
	//TODO public void addJabberPersonalAgentAvatar(){...;};
	//TODO public void addGoogleTalkPersonalAgentAvatar(){...;};
	//TODO public void addEmailAccount(){...;};
}

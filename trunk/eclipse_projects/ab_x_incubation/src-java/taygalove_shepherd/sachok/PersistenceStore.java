package taygalove_shepherd.sachok;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import taygalove_shepherd.GTD;
import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.util.ExceptionUtil;
import taygalove_shepherd.util.MsgBoxUtil;
import taygalove_shepherd.util.OSVersionUtil;

class PersistenceStore {
	private PersistenceSerializableStateObject ser;

	public void init(NamedCaller nc) {
		deserialize(nc);
	}

	private void deserialize(NamedCaller nc) {
		String filepath=getfilepath();
		String backupfilepath=getfilepathForBackupFile();
		File f=new File(filepath);
		File bak=new File(backupfilepath);
		if(!f.exists()&&!bak.exists()){
			ser=new PersistenceSerializableStateObject();
			return;
		}else{
			long filelenLong=f.length();
			if(filelenLong>Integer.MAX_VALUE)filelenLong=Integer.MAX_VALUE;
			else if(filelenLong<0)throw new AssertionError("negative length");
			int filelen=(int)filelenLong;
			byte[] buf=new byte[filelen];
			try{
				FileInputStream fis=new FileInputStream(f);
				try{
					DataInputStream dis=new DataInputStream(fis);
					dis.readFully(buf);
				}finally{
					try{fis.close();}catch(IOException e){
						MsgBoxUtil.showError(nc, "Cannot load preferences, error 1", e);//GTD localize message
					}
				}
				ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(buf));
				ser=(PersistenceSerializableStateObject)ois.readObject();
				return;
			}catch(Exception e){
				MsgBoxUtil.showError(nc, "Cannot load preferences, error 2", e);//GTD localize message
				try{
					deserializeFromBackup(nc);
				}catch(Exception e2){
					MsgBoxUtil.showError(nc, "Cannot load preferences, even from backup", e);//GTD localize message
//					MsgBoxUtil.showError(nc, "Cannot deserialize preferences from file "+f.getAbsolutePath()+", erasing preferences");
					//do not attempt to recover, just use the empty prefs;
					ser=new PersistenceSerializableStateObject();
					return;
				}
			}
		}
	}

	private static String userHome=System.getProperty("user.home");
	private String getfilepath() {
		return userHome+"/.net_sf_abproject/preferences.ser";
	}
	private String getfilepathForBackupFile() {
		return userHome+"/.net_sf_abproject/preferences.backup.ser";
	}

	private void deserializeFromBackup(NamedCaller nc) throws IOException, ClassNotFoundException {
		String backupfilepath=getfilepathForBackupFile();
		File bak=new File(backupfilepath);
		
		long filelenLong=bak.length();
		if(filelenLong>Integer.MAX_VALUE)filelenLong=Integer.MAX_VALUE;
		else if(filelenLong<0)throw new AssertionError("negative length of backup file");
		int filelen=(int)filelenLong;
		byte[] buf=new byte[filelen];
		FileInputStream fis=new FileInputStream(bak);
		try{
			DataInputStream dis=new DataInputStream(fis);
			dis.readFully(buf);
		}finally{
			try{fis.close();}catch(IOException e){
				MsgBoxUtil.showError(nc, "Cannot load preferences, error code B", e);//GTD localize message
			}
		}
		ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(buf));
		ser=(PersistenceSerializableStateObject)ois.readObject();
	}

	public void putBytes(NamedCaller nc,String key, byte[] bytes) {
		ser.records.put(key, bytes);
		serialize(nc);
	}

	private void serialize(NamedCaller nc) {
		File bak=new File(getfilepathForBackupFile());
		if(bak.exists())bak.delete();
		File main=new File(getfilepath());
		if(main.exists())main.renameTo(bak);
		main.getParentFile().mkdirs();
		try{
			FileOutputStream fos=new FileOutputStream(main);
			ObjectOutputStream oos=null;
			try{
				oos=new ObjectOutputStream(fos);
				oos.writeObject(ser);
			}finally{
				try{if(oos!=null)oos.close();}catch(IOException e){
					MsgBoxUtil.showError(nc, "Cannot store preferences, error 3: at "+main.getAbsolutePath(), e);//GTD localize message
				}
				try{fos.close();}catch(IOException e){
					MsgBoxUtil.showError(nc, "Cannot store preferences, error 4: at "+main.getAbsolutePath(), e);//GTD localize message
				}
			}
		}catch(Exception e){
			MsgBoxUtil.showError(nc, "Cannot store preferences, error 5: at "+main.getAbsolutePath(), e);//GTD localize message
		}
	}

	public byte[] getBytes(String key) {
		return ser.records.get(key);
	}

	public void removeNode(NamedCaller nc, String path) {
		Set<String> keys=ser.records.keySet();
		List<String> toremove=new LinkedList<String>();
		for(String key:keys){
			if(key.startsWith(path))toremove.add(key);
		}
		while(!toremove.isEmpty()){
			ser.records.remove(toremove.remove(0));
		}
		serialize(nc);
	}
}

package com.chat;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import com.holder.GroupItem;

/**
 * Created with IntelliJ IDEA. Date: 13/05/13 Time: 10:36
 */
public class Singleton {
	private static Singleton mInstance = null;
    private XMPPConnection connection;
    private GroupItem groupItem;
    private GroupItem allContact;
    private Message msg;
    private String SNSSTYPE;
    
	private Singleton() {
	}


	public static Singleton getInstance() {
		if (mInstance == null) {
			mInstance = new Singleton();
		}
		return mInstance;
	}


    public XMPPConnection getConnection() {
        return connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }

    public GroupItem getGroupItem() {
        return groupItem;
    }

    public void setGroupItem(GroupItem groupItem) {
        this.groupItem = groupItem;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }


	public GroupItem getAllContact() {
		return allContact;
	}


	public void setAllContact(GroupItem allContact) {
		this.allContact = allContact;
	}


	public String getSNSSTYPE() {
		return SNSSTYPE;
	}


	public void setSNSSTYPE(String sNSSTYPE) {
		SNSSTYPE = sNSSTYPE;
	}


}

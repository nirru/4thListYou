package com.chat;

import android.content.Context;
import android.util.Log;

import com.oxilo.listyou.constant.AppConstant;
import com.response.User;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.Iterator;

/**
 * Created by C-ShellWin on 2/11/2015.
 */
public class Chat {

    XMPPConnection connection;

    public XMPPConnection configureXmppConnection(final Context mContext, final User user) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(AppConstant.CHAT_SERVER_HOST,
                        Integer.parseInt(AppConstant.CHAT_SERVER_PORT), "");
                connConfig.setSendPresence(false);
                connConfig.setReconnectionAllowed(false);
                connection = new XMPPConnection(connConfig);
                MyConnectionListener connectionListener = new MyConnectionListener(user,connection);
                try {
                    SmackAndroid.init(mContext);
                    connection.connect();
                    Singleton.getInstance().setConnection(connection);
                    Chat.createUser(Singleton.getInstance().getConnection(), user);
//                    loginUser(Singleton.getInstance().getConnection(), user);
                    Thread.sleep(3000);
                    Log.i("XMPPClient",
                            "[SettingsDialog] Connected to " + connection.getHost());
                } catch (XMPPException ex) {
                    Log.e("XMPPClient", "[SettingsDialog] Failed to connect to "
                            + connection.getHost());
                    Log.e("XMPPClient", ex.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return connection;
    }

    public XMPPConnection reconnectXmpp(final Context mContext, final User user) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(AppConstant.CHAT_SERVER_HOST,
                        Integer.parseInt(AppConstant.CHAT_SERVER_PORT), "");
                connection = new XMPPConnection(connConfig);
                MyConnectionListener connectionListener = new MyConnectionListener(user,connection);
                try {
                    SmackAndroid.init(mContext);
                    connection.connect();
                    connection.addConnectionListener(connectionListener);
                    Singleton.getInstance().setConnection(connection);
//                    loginUser(Singleton.getInstance().getConnection(), user);
                    Thread.sleep(3000);
                    Log.i("XMPPClient",
                            "[SettingsDialog] Connected to " + connection.getHost());
                } catch (XMPPException ex) {
                    Log.e("XMPPClient", "[SettingsDialog] Failed to connect to "
                            + connection.getHost());
                    Log.e("XMPPClient", ex.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return connection;
    }


    public static void reconnectXmppConnection(final Context mContext, final User user) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(AppConstant.CHAT_SERVER_HOST,
                        Integer.parseInt(AppConstant.CHAT_SERVER_PORT), "");
                XMPPConnection connection = new XMPPConnection(connConfig);
                try {
                    SmackAndroid.init(mContext);
                    connection.connect();
                    Singleton.getInstance().setConnection(connection);
//                    Chat.loginUser(Singleton.getInstance().getConnection(), user);
                    connection.sendPacket(new Presence(Presence.Type.unavailable));
                    try {
                        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
                        connection.addPacketListener(new PacketListener() {
                            public void processPacket(Packet packet) {

                                Message message = (Message) packet;
                                if (message.getBody() != null) {
                                    String fromName = StringUtils.parseBareAddress(message
                                            .getFrom());
                                    Log.e("YES THIS IS HE ONE", "Got text [" + message.getBody()
                                            + "] from [" + fromName + "]");

                                }
                            }
                        }, filter);
//                        connection.sendPacket(new Presence(Presence.Type.available));
                        handleOfflineMessages(Singleton.getInstance().getConnection(), mContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Singleton.getInstance().getMsg() != null)
                        Singleton.getInstance().getConnection().sendPacket(Singleton.getInstance().getMsg());
                    Thread.sleep(3000);
                    Log.i("XMPPClient",
                            "[SettingsDialog] Connected to " + connection.getHost());
                } catch (XMPPException ex) {
                    Log.e("XMPPClient", "[SettingsDialog] Failed to connect to "
                            + connection.getHost());
                    Log.e("XMPPClient", ex.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static boolean checkXmppConnection() {
        if (Singleton.getInstance().getConnection().isConnected())
            return true;
        else
            return false;
    }

    public static boolean checkUser(XMPPConnection mXMPPConnection, User user) {

        boolean isUser = false;
        UserSearchManager search = new UserSearchManager(mXMPPConnection);

        Form searchForm;
        try {
            searchForm = search.getSearchForm("search."
                    + mXMPPConnection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);

            answerForm.setAnswer("search", user.id);

            org.jivesoftware.smackx.ReportedData data = search
                    .getSearchResults(answerForm,
                            "search." + mXMPPConnection.getServiceName());

            if (data.getRows() != null) {
                Iterator<ReportedData.Row> it = data.getRows();
                while (it.hasNext()) {
                    ReportedData.Row row = it.next();
                    Iterator iterator = row.getValues("jid");
                    if (iterator.hasNext()) {
                        isUser = true;
                        String value = iterator.next().toString();
                        Log.i("Iteartor values......", " " + value);
                    }
                    // Log.i("Iteartor values......"," "+value);
                }
            }
        } catch (XMPPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isUser;
    }

    public static void createUser(XMPPConnection connection, User user) {
        AccountManager accountManager = connection.getAccountManager();
        try {
            accountManager.createAccount(user.id, "12");
        } catch (XMPPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loginUser(XMPPConnection connection, User user) {
        try {

            connection.login(user.id, "12");
            Log.i("XMPPClient", "Logged in as " + connection.getUser());

            // Set the status to available
//            Presence presence = new Presence(Presence.Type.available);
//            Singleton.getInstance().getConnection().sendPacket(presence);

            int status = 1;
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus(connection.getUser() + status);
//                    Singleton.getInstance().getConnection().disconnect(presence);
            Singleton.getInstance().getConnection().sendPacket(presence);
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as "
                    + user.listyouid);
            Log.e("XMPPClient", ex.toString());
        }
    }


    public static void handleOfflineMessages(XMPPConnection connection, Context ctx) throws Exception {
        ProviderManager pm = ProviderManager.getInstance();
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        //  Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        OfflineMessageManager offlineMessageManager = new OfflineMessageManager(connection);

        if (!offlineMessageManager.supportsFlexibleRetrieval()) {
            Log.e("ZZZZZZZZZZZZZ!!!!", "Offline messages not supported");
            return;
        }

        if (offlineMessageManager.getMessageCount() == 0) {
            Log.e("XXXXXXXXXXXXXXX", "No offline messages found on server");
        } else {
            Iterator<org.jivesoftware.smack.packet.Message> msgs = offlineMessageManager
                    .getMessages();
            while (msgs.hasNext()) {
                org.jivesoftware.smack.packet.Message msg = msgs.next();
                String fullJid = msg.getFrom();
                String bareJid = StringUtils.parseBareAddress(fullJid);
                String messageBody = msg.getBody();
                if (messageBody != null) {
                    Log.e("RETREIVED MESSAGE111", "Retrieved offline message from " + messageBody);

                }
            }
            offlineMessageManager.deleteMessages();
        }
    }


    private static void getPacketListener() {
        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
        Singleton.getInstance().getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {

                Message message = (Message) packet;
                if (message.getBody() != null) {
                    String fromName = StringUtils.parseBareAddress(message
                            .getFrom());
                    Log.e("XMPPClient", "Got text [" + message.getBody()
                            + "] from [" + fromName + "]");

                }
            }
        }, filter);
    }

    private class MyConnectionListener implements ConnectionListener {
        private User user;
        private XMPPConnection connection;

        private MyConnectionListener(User user, XMPPConnection connection) {
            this.user = user;
            this.connection = connection;
        }

        @Override
        public void connectionClosed() {

        }

        @Override
        public void connectionClosedOnError(Exception e) {

        }

        @Override
        public void reconnectingIn(int i) {

        }

        @Override
        public void reconnectionSuccessful() {
            Log.e("SUCESS FULL IS CALLED", "YSS");
            loginUser(connection, user);
        }

        @Override
        public void reconnectionFailed(Exception e) {

        }
    }

}

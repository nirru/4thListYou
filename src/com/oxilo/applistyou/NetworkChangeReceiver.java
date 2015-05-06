package com.oxilo.applistyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.chat.Singleton;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.util.Util;

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
import org.jivesoftware.smack.util.StringUtils;

/**
 * Created by C-ShellWin on 2/20/2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

      XMPPConnection connection1;
    @Override
    public void onReceive(final Context mContext, final Intent intent) {

        if (checkInternet(mContext)) {
//            Log.e("TREEEE","" + "INTERNETE IS PRESEENT");
            Toast.makeText(mContext, "Network Available Do operations", Toast.LENGTH_LONG).show();
            int pref_screen_id = Util.getScreennavigation(mContext);
            if (pref_screen_id == 2) {
                Log.e("TREEEE", "" + "INTERNETE IS PRESEENT");
                reconnectToXmppServer(mContext, pref_screen_id);
//                Singleton.getInstance().getConnection().sendPacket(new Presence(Presence.Type.unavailable));

            }

        }
        else {
            Log.e("YSSSS", "" + "INTERNETE IS NOT PRESEENT");

        }

    }


    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    private void reconnectToXmppServer(Context mContext, int pref_screen_id) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.oxilo.listyou_app_country_code",
                SplashActivity.MODE_PRIVATE);
        String id = sharedPreferences.getString(AppConstant.REG_ID, "");
        User user = Util.saveParcel(Util.readFromPrefs(mContext), id);
//        Chat.reconnectXmppConnection(mContext, user);
        logout(mContext, user);
    }

    public void logout(final Context mContext, final User user) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(AppConstant.CHAT_SERVER_HOST,
                        Integer.parseInt(AppConstant.CHAT_SERVER_PORT), "");
                final XMPPConnection connection = new XMPPConnection(connConfig);

                try {
                    SmackAndroid.init(mContext);
                    connection.addConnectionListener(new ConnectionListener() {
                        @Override
                        public void connectionClosed() {
                            Log.e("YHHAHAHAHHAHD","" + "CONNECTION");
                            int status = 0;
                            Presence presence = new Presence(Presence.Type.unavailable);
                            presence.setStatus(connection.getUser() + status);
                            connection.sendPacket(presence);
                        }

                        @Override
                        public void connectionClosedOnError(Exception e) {

                        }

                        @Override
                        public void reconnectingIn(int i) {

                        }

                        @Override
                        public void reconnectionSuccessful() {
                            Log.e("CONNECTION SUCCESFULL", "" + "YES");
//                            login(mContext,user);
                        }

                        @Override
                        public void reconnectionFailed(Exception e) {

                        }
                    });
                    connection.connect();
                    connection1 = connection;
                    Singleton.getInstance().setConnection(connection);
                    offLineUser(connection, user, mContext);
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


    public void login(final Context mContext, final User user) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(AppConstant.CHAT_SERVER_HOST,
                        Integer.parseInt(AppConstant.CHAT_SERVER_PORT), "");
                final XMPPConnection connection = new XMPPConnection(connConfig);
                try {
                    SmackAndroid.init(mContext);
                    connection.addConnectionListener(new ConnectionListener() {
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
                            Log.e("CONNECTION SUCCESFULL", "" + "YES");
                            setConnection(connection);
                        }

                        @Override
                        public void reconnectionFailed(Exception e) {

                        }
                    });
                    connection.connect();

                    onLineUser(connection, user);
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

    public void offLineUser(XMPPConnection connection, User user, Context mContext) {
        try {

            connection.login(user.id, "12");
            Log.e("NETWORK RECEIVER", "Logged in as " + connection.getUser());

            // Set the status to available
//            Presence presence = new Presence(Presence.Type.available);
//            Singleton.getInstance().getConnection().sendPacket(presence);

            int status = 0;
            Presence presence = new Presence(Presence.Type.unavailable);
            presence.setStatus(connection.getUser() + status);
            connection.sendPacket(presence);

            setConnection(connection);

//            login(mContext,user);
//
//            setConnection(connection);
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as "
                    + user.listyouid);
            Log.e("XMPPClient", ex.toString());
        }
    }

    public void onLineUser(XMPPConnection connection, User user) {
        try {

            connection.login(user.id, "12");
            Log.e("ONLINE RECIEVER", "Logged in as " + connection.getUser());

            // Set the status to available
            int status = 1;
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus(connection.getUser() + status);
            connection.sendPacket(presence);

        }
        catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as "
                    + user.listyouid);
            Log.e("XMPPClient", ex.toString());
        }
    }


    /**
     * Called by Settings dialog when a connection is establised with the XMPP server
     *
     * @param
     */
    public void setConnection(XMPPConnection connection) {
        Log.e("RE1234", "IS CALLED");
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.e("OFLLINE MESSAGE ", "Got text [" + message.getBody() + "] from [" + fromName + "]");

                    }
                }
            }, filter);
        }
    }
}

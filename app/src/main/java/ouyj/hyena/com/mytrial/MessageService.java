package ouyj.hyena.com.mytrial;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import ouyj.hyena.com.mytrial.utils.Constants;

public class MessageService extends Service {

    private final Messenger enger = new Messenger(new MsgHandler());

    public MessageService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        //返回一个IBinder对象给客户端
        return enger.getBinder();
    }



    private static class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_CLIENT:

                    Bundle bundle = new Bundle();
                    bundle.putString("chat", "消息已收到，稍后回复！");

                    Messenger client = msg.replyTo;
                    Message relpyMessage = Message.obtain(null, Constants.MSG_SERVER);
                    relpyMessage.setData(bundle);
                    try {
                        client.send(relpyMessage);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }




}

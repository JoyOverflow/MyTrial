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

    //创建服务端信使对象（封装handler）
    private final Messenger serverMessenger = new Messenger(new MsgHandler());

    public MessageService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        //返回一个IBinder对象给客户端
        return serverMessenger.getBinder();
    }

    /**
     * 可接收客户端数据进行处理，并向客户端发送Message
     */
    private static class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //接收客户端的消息并处理
                case Constants.MSG_CLIENT:
                    //获取客户端的请求数据
                    String str=msg.getData().getString("clientMessenger");
                    str=String.format("消息：【%s 】已收到，稍后回复！",str);
                    Bundle bundle = new Bundle();
                    bundle.putString("server", str);

                    //取出客户端的（服务端回复）处理对象
                    Messenger clientMessenger = msg.replyTo;
                    Message message = Message.obtain(null, Constants.MSG_SERVER);
                    message.setData(bundle);
                    try {
                        //回复客户端的消息
                        clientMessenger.send(message);
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

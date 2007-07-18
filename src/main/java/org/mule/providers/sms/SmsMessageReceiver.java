package org.mule.providers.sms;

import java.io.PrintStream;
import java.util.LinkedList;
import org.mule.impl.MuleMessage;
import org.mule.providers.AbstractMessageReceiver;
import org.mule.providers.PollingMessageReceiver;
import org.mule.umo.UMOComponent;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.provider.UMOConnector;
import org.smslib.*;

// Referenced classes of package org.mule.providers.sms:
//            SmsConnector

public class SmsMessageReceiver extends PollingMessageReceiver
{

    public SmsMessageReceiver(UMOConnector connector, UMOComponent component, UMOEndpoint endpoint, String gsmCom, Integer gsmBaudrate, String gsmManufacturer, String gsmModel, 
            Boolean reconnect, Long pollingFrequency)
        throws InitialisationException
    {
        super(connector, component, endpoint, pollingFrequency);
        this.gsmCom = gsmCom;
        this.gsmBaudrate = gsmBaudrate.intValue();
        this.gsmManufacturer = gsmManufacturer;
        this.gsmModel = gsmModel;
        this.reconnect = reconnect.booleanValue();
    }

    public void poll()
        throws Exception
    {
        System.out.println("Reading sms...");
        LinkedList msgList = new LinkedList();
        if(reconnect)
            connectToService();
        srv.readMessages(msgList, 2);
        for(int i = 0; i < msgList.size(); i++)
        {
            CIncomingMessage msg = (CIncomingMessage)msgList.get(i);
            if(((SmsConnector)super.connector).isDeleteReadMessages())
                srv.deleteMessage(msg);
            MuleMessage message = new MuleMessage(super.connector.getMessageAdapter(msg));
            routeMessage(message, super.endpoint.isSynchronous());
        }

        if(reconnect)
            disconnectFromService();
        System.out.println("Sms read.");
    }

    public void doConnect()
        throws Exception
    {
        if(!reconnect)
            connectToService();
    }

    public void doDisconnect()
        throws Exception
    {
        if(!reconnect)
            disconnectFromService();
    }

    private void disconnectFromService()
        throws Exception
    {
        System.out.println("Closing mobile connection");
        srv.disconnect();
    }

    private void connectToService()
        throws Exception
    {
        srv = new CService(gsmCom, gsmBaudrate, gsmManufacturer, gsmModel);
        srv.connect();
        System.out.println("Mobile Device Information: ");
        System.out.println("\tManufacturer  : " + srv.getDeviceInfo().getManufacturer());
        System.out.println("\tModel         : " + srv.getDeviceInfo().getModel());
        System.out.println("\tSerial No     : " + srv.getDeviceInfo().getSerialNo());
        System.out.println("\tIMSI          : " + srv.getDeviceInfo().getImsi());
        System.out.println("\tS/W Version   : " + srv.getDeviceInfo().getSwVersion());
        System.out.println("\tBattery Level : " + srv.getDeviceInfo().getBatteryLevel() + "%");
        System.out.println("\tSignal Level  : " + srv.getDeviceInfo().getSignalLevel() + "%");
        System.out.println("\tGPRS Status   : " + (srv.getDeviceInfo().getGprsStatus() ? "Enabled" : "Disabled"));
        System.out.println("");
    }

    private String gsmCom;
    private int gsmBaudrate;
    private String gsmManufacturer;
    private String gsmModel;
    private boolean reconnect;
    private CService srv;
}

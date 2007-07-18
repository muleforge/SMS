// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SmsMessageDispatcher.java

package org.mule.providers.sms;

import java.io.PrintStream;
import org.mule.providers.AbstractMessageDispatcher;
import org.mule.umo.*;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.provider.UMOMessageAdapter;
import org.smslib.*;

// Referenced classes of package org.mule.providers.sms:
//            SmsConnector

public class SmsMessageDispatcher extends AbstractMessageDispatcher
{

    public SmsMessageDispatcher(UMOImmutableEndpoint endpoint)
    {
        super(endpoint);
        connector = (SmsConnector)endpoint.getConnector();
    }

    protected void doDispose()
    {
    }

    protected void doDispatch(UMOEvent event)
        throws Exception
    {
        String receiver = event.getMessage().getStringProperty("SMS_RECEIVER", null);
        if(receiver == null)
        {
            receiver = super.endpoint.getEndpointURI().getAddress();
            if(receiver == null)
                throw new UnsupportedOperationException("Unknow receiver for sms, set SmsProperties.SMS_RECEIVER property on message.");
        }
        System.out.println("Sending message to " + receiver);
        boolean statusreport = event.getMessage().getBooleanProperty("SMS_STATUSREPORT", false);
        boolean flash = event.getMessage().getBooleanProperty("SMS_FLASH", false);
        COutgoingMessage msg = new COutgoingMessage(receiver, event.getTransformedMessageAsString());
        msg.setMessageEncoding(1);
        msg.setStatusReport(statusreport);
        msg.setFlashSms(flash);
        msg.setValidityPeriod(8);
        if(connector.isReconnect())
            connectToService();
        srv.sendMessage(msg);
        if(connector.isReconnect())
            disconnectFromService();
        System.out.println("Message sent.");
    }

    protected UMOMessage doSend(UMOEvent event)
        throws Exception
    {
        doDispatch(event);
        return event.getMessage();
    }

    protected void doConnect(UMOImmutableEndpoint arg0)
        throws Exception
    {
        if(!connector.isReconnect())
            connectToService();
    }

    protected void doDisconnect()
        throws Exception
    {
        if(!connector.isReconnect())
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
        srv = new CService(connector.getGsmCom(), connector.getGsmBaudrate(), connector.getGsmManufacturer(), connector.getGsmModel());
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

    protected UMOMessage doReceive(UMOImmutableEndpoint endpoint, long timeout)
        throws Exception
    {
        return null;
    }

    public Object getDelegateSession()
        throws UMOException
    {
        return null;
    }

    private SmsConnector connector;
    private CService srv;
}

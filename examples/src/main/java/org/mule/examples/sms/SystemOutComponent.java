
package org.mule.examples.sms;

import java.io.PrintStream;
import org.mule.impl.MuleMessage;
import org.mule.umo.UMOEventContext;
import org.mule.umo.lifecycle.Callable;

public class SystemOutComponent
    implements Callable
{

    public SystemOutComponent()
    {
    }

    public Object onCall(UMOEventContext context)
        throws Exception
    {
        MuleMessage message = (MuleMessage)context.getMessage();
        System.out.println("Sender: " + message.getStringProperty("SMS_SENDER", ""));
        System.out.println("Date: " + message.getStringProperty("SMS_DATE", ""));
        System.out.println("Encoding: " + message.getStringProperty("SMS_ENCODING", ""));
        System.out.println("ID: " + message.getStringProperty("SMS_ID", ""));
        try
        {
            System.out.println("Message: " + message.getPayloadAsString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return message;
    }
}

package org.mule.providers.sms;

import org.mule.providers.AbstractMessageAdapter;
import org.mule.umo.provider.MessageTypeNotSupportedException;
import org.smslib.CIncomingMessage;
import org.smslib.CMessage;

public class SmsMessageAdapter extends AbstractMessageAdapter
{

    public SmsMessageAdapter(Object obj)
        throws MessageTypeNotSupportedException
    {
        CIncomingMessage message = null;
        if(obj instanceof CIncomingMessage)
            message = (CIncomingMessage)obj;
        else
            throw new MessageTypeNotSupportedException(message, org.mule.providers.sms.SmsMessageAdapter.class);
        text = message.getText();
        if(message.getDate() != null)
            setProperty("SMS_DATE", message.getDate());
        setProperty("SMS_ENCODING", Integer.valueOf(message.getMessageEncoding()));
        if(message.getId() != null)
            setProperty("SMS_ID", message.getId());
        if(message.getOriginator() != null)
            setProperty("SMS_SENDER", message.getOriginator());
    }

    public String getPayloadAsString(String encoding)
        throws Exception
    {
        return text.toString();
    }

    public byte[] getPayloadAsBytes()
        throws Exception
    {
        return text.toString().getBytes();
    }

    public Object getPayload()
    {
        return text;
    }

    private static final long serialVersionUID = 0xc46645da79d115d2L;
    private String text;
}

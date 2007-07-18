

package org.mule.examples.sms;

import org.mule.impl.MuleMessage;
import org.mule.transformers.AbstractTransformer;
import org.mule.umo.transformer.TransformerException;

public class StringToSmsTransformer extends AbstractTransformer
{

    public StringToSmsTransformer()
    {
    }

    protected Object doTransform(Object object, String encoding)
        throws TransformerException
    {
        MuleMessage message = new MuleMessage(object.toString());
        message.setProperty("SMS_STATUSREPORT", Boolean.FALSE);
        message.setProperty("SMS_FLASH", Boolean.FALSE);
        message.setProperty("SMS_RECEIVER", "+32496250169");
        return message;
    }
}

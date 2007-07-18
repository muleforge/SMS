package org.mule.providers.sms;

import org.mule.umo.UMOException;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.provider.UMOMessageDispatcher;
import org.mule.umo.provider.UMOMessageDispatcherFactory;

// Referenced classes of package org.mule.providers.sms:
//            SmsMessageDispatcher

public class SmsMessageDispatcherFactory
    implements UMOMessageDispatcherFactory
{

    public SmsMessageDispatcherFactory()
    {
    }

    public UMOMessageDispatcher create(UMOImmutableEndpoint endpoint)
        throws UMOException
    {
        return new SmsMessageDispatcher(endpoint);
    }
}

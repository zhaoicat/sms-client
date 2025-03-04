package com.chinamobile.cmos.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinamobile.cmos.MessageReceiver;
import com.chinamobile.cmos.SmsClient;
import com.chinamobile.cmos.SmsClientBuilder;
import com.zx.sms.BaseMessage;
import com.zx.sms.codec.smgp.msg.SMGPSubmitMessage;
import com.zx.sms.connect.manager.EndpointEntity.ChannelType;
import com.zx.sms.connect.manager.smgp.SMGPClientEndpointEntity;

public class TestSMGPClient {
	private static final Logger logger = LoggerFactory.getLogger(TestSMGPClient.class);

	private ExecutorService executor =  Executors.newFixedThreadPool(10);
	@Test
	public void testcmpp() throws Exception {
		SMGPClientEndpointEntity client = new SMGPClientEndpointEntity();
		client.setId("smgpclient");
		client.setHost("127.0.0.1");
		client.setPort(9890);
		client.setClientID("333");
		client.setPassword("0555");
		client.setChannelType(ChannelType.DUPLEX);

		client.setMaxChannels((short)2);
		client.setRetryWaitTimeSec((short)100);
		client.setUseSSL(false);
		client.setReSendFailMsg(false);
		client.setClientVersion((byte)0x13);
		
		SmsClientBuilder builder = new SmsClientBuilder();
		final SmsClient smsClient = builder.entity(client).receiver(new MessageReceiver() {

			public void receive(BaseMessage message) {
				logger.info(message.toString());
				
			}}).build();
		Future future = null;
		for (int i = 0; i < 5; i++) {
			 future = executor.submit(new Runnable() {

				public void run() {
					SMGPSubmitMessage pdu = new SMGPSubmitMessage();
					pdu.setSrcTermId("10086");
			        pdu.setDestTermIdArray("13800138000");
			        pdu.setMsgContent("SMGPSubmitMessage");
			        pdu.setNeedReport(true);
					try {
						smsClient.send(pdu, 1000);
					} catch (Exception e) {
						logger.info("send ", e);
					}
				}
				
			});
		}
		future.get();
		Thread.sleep(5000);
		
	}
}

package test;

import org.junit.Test;

import com.alibaba.rocketmq.tools.command.MQAdminStartup;

public class QueryMsgByIdTest {
	
	@Test
	public void test(){
		String[] args = new String[]{
				"queryMsgById",
				"-n",
				"127.0.0.1:9876",
				"-i",
				"AC143C3919DC31221BE28BE5F1310000"
		};
		MQAdminStartup.main(args);
	}

}

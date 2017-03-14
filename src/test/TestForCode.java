package test;

import org.agl.webContent.exception.InitPropertiesException;
import org.agl.webContent.utils.ServerInit;

public class TestForCode {

	public static void main(String[] args) {
		System.out.println(ServerInit.getRequestChartSet());
		System.out.println(ServerInit.getController());
	}

}

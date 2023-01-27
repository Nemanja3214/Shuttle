package util;

public class DriverSetup {
	private static final String DRIVER_EDGE = "webdriver.edge.driver";
	private static final String DRIVER_EDGE_LOC = "src/test/resources/driver/msedgedriver.exe";
	private static final String DRIVER_FIREFOX = "webdriver.gecko.driver";
	private static final String DRIVER_FIREFOX_LOC = "";
	
	public static void useEdge() {
		System.setProperty(DRIVER_EDGE, DRIVER_EDGE_LOC);
	}
	
	public static void useFirefox() {
		System.setProperty(DRIVER_FIREFOX, DRIVER_FIREFOX_LOC);
	}
}

package models;

import java.net.URL;

public class PollingProperties {
	public PollingProperties() {
	}
	public PollingProperties(Long pollingPeriod, URL pollingUrl,
			String pollingAuthenticationKey) {
		super();
		this.pollingPeriod = pollingPeriod;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
	}
	public long pollingPeriod=0L;
	public long lastPolled=0L;
  public URL pollingUrl=null;
  public String pollingAuthenticationKey=null;
  
	public void setPeriod(Long period) {
		this.pollingPeriod = period;
	}
}

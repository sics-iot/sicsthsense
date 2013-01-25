package se.sics.sense.filebased;

public class Resource {
	public final String  protocol,
			service,
			user,
			tag, 
			devices,
			baseUrl,
			fullUrl;
	private String	preJson="",postJson="";
	
	public Resource(String url) {
		String[] p = url.split("/+");
		int i=0;
		protocol = p[0].endsWith(":") ? p[i++]+"//" : "http://";
		service = p[i++]+"/"+p[i++];
		user = p[i++];
		tag = p[i++];
		String a="",b="";		
		for (; i < p.length; i++) {
			a=b+p[i];
			b=a+"/";
			preJson+="{\""+p[i]+"\":";
			postJson+="}";
		}
		devices=a;
		
		baseUrl=protocol+service+"/"+user+"/"+tag+"/";//Trailing slash is important
		fullUrl=baseUrl+devices;

	}
	
	public String getJson(String val){
		return preJson +val+postJson;
	}
	
	@Override
	public String toString() {
		String s = "protocol: "+protocol;
		s += ", service: "+service;
		s += ", user: "+user;
		s += ", resource: "+tag;
		s += ", sensor: "+devices;
		s += ", baseUrl: "+baseUrl;
		
		return s;
				
	}
	
	public static void main(String[] args) {
		Resource r = new Resource("sense.sics.se/streams/simon/phone/sensors/orientation/x");
		System.out.println(r);
	}
}

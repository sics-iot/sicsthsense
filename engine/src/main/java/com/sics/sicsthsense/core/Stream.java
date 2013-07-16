package com.sics.sicsthsense.core;

public class Stream {
	private long id;
	private String label;
	private char type;
	private double latitude;
	private double longitude;
	private String description;
	private boolean public_access;
	private boolean public_search;
	private boolean frozen;
	private int history_size;
	private int last_updated;
	private String secret_key;
	private int owner_id;
	private int resource_id;
	private int version;

    public Stream() {}
    public Stream(long id, String label) {
			this();
      this.id			= id;
      this.label	= label;
    }
    public Stream( long id,
			String label,
			char type,
			double latitude,
			double longitude,
			String description,
			boolean public_access,
			boolean public_search,
			boolean frozen,
			int history_size,
			int last_updated,
			String secret_key,
			int owner_id,
			int resource_id,
			int version
		) {
		this();
		}

	public long getId()								{ return id; }
	public String getLabel()					{ return label; }
	public char getType()							{ return type; }
	public double getLatitude()				{ return latitude; }
	public double getLongitude()			{ return longitude; }
	public String getDescription()		{ return description; }
	public boolean getPublic_access() { return public_access; }
	public boolean getPublic_search() { return public_search; }
	public boolean getFrozen()				{ return frozen; }
	public int getHistory_size()			{ return history_size; }
	public int getLast_updated()			{ return last_updated; }
	public String getSecret_key()			{ return secret_key; }
	public int getOwner_id()					{ return owner_id; }
	public int getResource_id()				{ return resource_id; }
	public int getVersion()						{ return version; }

	public void setId(long id)										{ this.id = id; }
	public void setLabel(String label)						{ this.label = label; }
	public void setType(char type)								{ this.type = type; }
	public void setLatitude(double latitude)			{ this.latitude = latitude; }
	public void setLongitude(double longitude)		{ this.longitude = longitude; }
	public void setDescription(String description)			{ this.description = description; }
	public void setPublic_access(boolean public_access) { this.public_access = public_access; }
	public void setPublic_search(boolean public_search) { this.public_search = public_search; }
	public void setFrozen(boolean frozen)					{ this.frozen = frozen; }
	public void setHistory_size(int history_size) { this.history_size = history_size; }
	public void setLast_updated(int last_updated) { this.last_updated = last_updated; }
	public void setSecret_key(String secret_key)	{ this.secret_key = secret_key; }
	public void setOwner_id(int owner_id)					{ this.owner_id = owner_id; }
	public void setResource_id(int resource_id)		{ this.resource_id = resource_id; }
	public void setVersion(int version)						{ this.version = version; }
}

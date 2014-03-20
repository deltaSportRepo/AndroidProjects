package domain;

public class Region {

	private int regionId;
	private String name;
	
	public Region(int regionId, String name) {
		super();
		this.regionId = regionId;
		this.name = name;
	}
	
	public Region() {
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}

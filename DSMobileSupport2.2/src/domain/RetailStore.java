package domain;

public class RetailStore {

	private int retailStoreId;
	private String name;
	private int retailGroupId;

	public RetailStore() {
	}

	public RetailStore(int retailStoreId, String name, int retailGroupId) {
		this.retailStoreId = retailStoreId;
		this.name = name;
		this.retailGroupId = retailGroupId;
	}

	public int getRetailStoreId() {
		return retailStoreId;
	}

	public void setRetailStoreId(int retailStoreId) {
		this.retailStoreId = retailStoreId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getRetailGroupId() {
		return retailGroupId;
	}

	public void setRetailGroupId(int retailGroupId) {
		this.retailGroupId = retailGroupId;
	}
	
	@Override
	public String toString() {
		return name;
	}
}

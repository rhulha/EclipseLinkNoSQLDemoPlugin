package net.raysforge.eclipselink.hashmap;

import java.util.HashMap;

import javax.resource.cci.MappedRecord;

@SuppressWarnings("rawtypes")
public class DemoMappedRecord extends HashMap implements MappedRecord {

	protected String description;
	protected String name;

	public DemoMappedRecord() {
		super();
		this.name = "DemoMappedRecord";
		this.description = "DemoMappedRecord";
	}

	public String getRecordShortDescription() {
		return description;
	}

	public void setRecordShortDescription(String description) {
		this.description = description;
	}

	public String getRecordName() {
		return name;
	}

	public void setRecordName(String name) {
		this.name = name;
	}
}

package net.raysforge.eclipselink.hashmap;

import java.util.ArrayList;

import javax.resource.cci.IndexedRecord;

@SuppressWarnings("rawtypes")
public class DemoListRecord extends ArrayList implements IndexedRecord {
    protected String description;
    protected String name;

    public DemoListRecord() {
        super();
        this.name = "demo record list";
        this.description = "List of demo records";
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
    
    @SuppressWarnings("unchecked")
	public void addRecord(DemoMappedRecord r) {
    	add(r);
    }
}

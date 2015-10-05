package net.raysforge.eclipselink.hashmap;

import javax.resource.ResourceException;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.RecordFactory;

import org.eclipse.persistence.exceptions.ValidationException;

public class DemoRecordFactory implements RecordFactory {

	@Override
	public IndexedRecord createIndexedRecord(String arg0) throws ResourceException {
        throw ValidationException.operationNotSupported("createIndexedRecord");
	}

	@Override
	public MappedRecord createMappedRecord(String arg0) throws ResourceException {
		return new DemoMappedRecord();
	}

}

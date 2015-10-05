package net.raysforge.eclipselink.hashmap;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;

import org.eclipse.persistence.eis.EISException;

public class DemoInteraction implements Interaction {

	private DemoConnection connection;

	public DemoInteraction(DemoConnection connection) {
		this.connection = connection;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public Record execute(InteractionSpec spec, Record record) throws ResourceException {
		if (!(spec instanceof DemoInteractionSpec)) {
			throw EISException.invalidInteractionSpecType();
		}
		if (!(record instanceof DemoMappedRecord)) {
			throw EISException.invalidRecordType();
		}
		DemoInteractionSpec iSpec = (DemoInteractionSpec) spec;
		DemoMappedRecord input = (DemoMappedRecord) record;
		DemoOperation operation = iSpec.getOperation();
		String tableName = iSpec.getTable();
		
		if (operation == null) {
			throw new ResourceException("operation must be set");
		}
		if (tableName == null) {
			throw new ResourceException("table name must be set");
		}
		switch (operation) {
			case INSERT:
				connection.write(iSpec, tableName, input);
				return input;
			case REMOVE:
				connection.remove(iSpec, tableName, input);
				return null;
			case FIND:
				return connection.find(iSpec, tableName, input);
			default:
				throw new ResourceException("Invalid operation: " + operation);
		}
	}

	// This is for entity updates
	@Override
	public boolean execute(InteractionSpec spec, Record inputRec, Record searchRec) throws ResourceException {
		if (!(spec instanceof DemoInteractionSpec)) {
			throw EISException.invalidInteractionSpecType();
		}
		if (!(inputRec instanceof DemoMappedRecord) || !(searchRec instanceof DemoMappedRecord)) {
			throw EISException.invalidRecordType();
		}
		DemoInteractionSpec iSpec = (DemoInteractionSpec) spec;
		DemoMappedRecord mappedInputRec = (DemoMappedRecord) inputRec;
		DemoMappedRecord mappedSearchRec = (DemoMappedRecord) searchRec;
		DemoOperation operation = iSpec.getOperation();
		String tableName = iSpec.getTable();
		if (operation == null) {
			throw new ResourceException("operation must be set");
		}
		if (tableName == null) {
			throw new ResourceException("DB name must be set");
		}
		if (operation != DemoOperation.UPDATE) {
			throw new ResourceException("Invalid operation: " + operation);
		}
		connection.update(iSpec, tableName, mappedSearchRec, mappedInputRec);
		return true;
	}

	@Override
	public void clearWarnings() throws ResourceException {
	}

	@Override
	public void close() throws ResourceException {
	}

	@Override
	public ResourceWarning getWarnings() throws ResourceException {
		return null;
	}

}

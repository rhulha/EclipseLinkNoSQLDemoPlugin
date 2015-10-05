package net.raysforge.eclipselink.hashmap;

import javax.resource.ResourceException;
import javax.resource.cci.LocalTransaction;

public class DemoTransaction implements LocalTransaction {

	protected DemoConnection connection;

	public DemoTransaction(DemoConnection connection) {
		this.connection = connection;
	}

	@Override
	public void begin() throws ResourceException {
	}

	@Override
	public void commit() throws ResourceException {
	}

	@Override
	public void rollback() throws ResourceException {
	}

}

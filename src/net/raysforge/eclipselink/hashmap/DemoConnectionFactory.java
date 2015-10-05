package net.raysforge.eclipselink.hashmap;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;

public class DemoConnectionFactory implements ConnectionFactory {

	@Override
	public void setReference(Reference ref) {
	}

	@Override
	public Reference getReference() throws NamingException {
		return new Reference(getClass().getName());
	}

	@Override
	public Connection getConnection() throws ResourceException {
		return getConnection(new DemoJCAConnectionSpec());
	}

	@Override
	public Connection getConnection(ConnectionSpec spec) throws ResourceException {
		DemoJCAConnectionSpec connectionSpec = (DemoJCAConnectionSpec) spec;
		return new DemoConnection(connectionSpec);
	}

	@Override
	public ResourceAdapterMetaData getMetaData() throws ResourceException {
		return new DemoAdapterMetaData();
	}

	@Override
	public RecordFactory getRecordFactory() throws ResourceException {
		return new DemoRecordFactory();
	}

}

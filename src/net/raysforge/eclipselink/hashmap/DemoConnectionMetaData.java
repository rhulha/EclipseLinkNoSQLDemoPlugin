package net.raysforge.eclipselink.hashmap;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionMetaData;

public class DemoConnectionMetaData implements ConnectionMetaData {

	protected DemoConnection connection;

	public DemoConnectionMetaData(DemoConnection connection) {
		this.connection = connection;
	}

	@Override
	public String getEISProductName() throws ResourceException {
		return "Demo JPA adapter";
	}

	@Override
	public String getEISProductVersion() throws ResourceException {
		return "2.1";
	}

	@Override
	public String getUserName() throws ResourceException {
		return "";
	}

}

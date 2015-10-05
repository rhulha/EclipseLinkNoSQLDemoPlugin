package net.raysforge.eclipselink.hashmap;

import java.util.Properties;

import javax.resource.cci.Connection;

import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;

public class DemoConnectionSpec extends EISConnectionSpec {

	public DemoConnectionSpec() {
		super();
	}

	/**
	 * Connect with the specified properties and return the Connection.
	 */
	public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
		if (this.connectionFactory == null && this.name == null) {
			this.connectionFactory = new DemoConnectionFactory();
		}
		if (!properties.isEmpty()) {
			if (this.connectionSpec == null) {
				this.connectionSpec = new DemoJCAConnectionSpec();
			}
		}
		return super.connectToDataSource(accessor, properties);
	}
}

package net.raysforge.eclipselink.hashmap;

import javax.resource.cci.InteractionSpec;

import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.eis.interactions.EISInteraction;
import org.eclipse.persistence.internal.databaseaccess.QueryStringCall;
import org.eclipse.persistence.internal.expressions.SQLStatement;
import org.eclipse.persistence.queries.DatabaseQuery;

public class DemoInteractionSpec implements InteractionSpec {

	private DemoOperation operation;
	private String table;
	private String[] keys;
	private DatabaseQuery query;
	public final SQLStatement statement;
	
	public DemoInteractionSpec(EISInteraction interaction) {
        Object operation = interaction.getProperty(DemoPlatform.OPERATION);
        if (interaction.isQueryStringCall()) {
            System.out.println("EVAL ? " + ((QueryStringCall)interaction).getQueryString());
        }
        if (operation == null) {
            throw new EISException("'" + DemoPlatform.OPERATION + "' property must be set on the query's interation.");
        }
        if (operation instanceof String) {
            operation = DemoOperation.valueOf((String)operation);
        }
        this.operation = (DemoOperation)operation;
        
        query=interaction.getQuery();
        
        //Vector arguments = interaction.getArguments(); // priamry keys ?
        
        statement = (SQLStatement) interaction.getProperty("SQLStatement");
        //System.out.println("SQLStatement: " + statement);

        this.table=(String) interaction.getProperty(DemoPlatform.TABLE);
        this.keys = (String[]) interaction.getProperty(DemoPlatform.KEYS);
	}

	public DemoOperation getOperation() {
		return operation;
	}

	public void setOperation(DemoOperation operation) {
		this.operation = operation;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setQuery(DatabaseQuery query) {
		this.query = query;
	}
	
	public DatabaseQuery getQuery() {
		return this.query;
	}
	
	public String[] getKeys() {
		return keys;
	}

}

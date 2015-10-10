package net.raysforge.eclipselink.hashmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.ResultSetInfo;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

public class DemoConnection implements Connection {

	// tablename, list of MappedRecords
	public static final HashMap<String, ArrayList<HashMap<String, Object>>> database = new HashMap<String, ArrayList<HashMap<String, Object>>>(); 
	private void ensureDatabaseExists(String tableName) {
		if (!database.containsKey(tableName)) {
			database.put(tableName, new ArrayList<HashMap<String, Object>>());
		}
	}

	protected DemoJCAConnectionSpec connectionSpec;
	private DemoTransaction transaction;

	public DemoConnection(DemoJCAConnectionSpec connectionSpec) {
		this.connectionSpec = connectionSpec;
		this.transaction = new DemoTransaction(this);
	}

	@SuppressWarnings("unchecked")
	public void write(DemoInteractionSpec spec, String tableName, DemoMappedRecord mapped) throws ResourceException {
		ensureDatabaseExists(tableName);
		insertIdentityIfMissing(spec, mapped);
		database.get(tableName).add(mapped);
	}

	@SuppressWarnings("unchecked")
	public void update(DemoInteractionSpec spec, String tableName, DemoMappedRecord which, DemoMappedRecord with) throws ResourceException {
		ensureDatabaseExists(tableName);
		if (spec.getKeys().length != 1)
			throw new ResourceException("one and only one @Id field is currently supported.");
		String idFieldName = spec.getKeys()[0].split(" ")[1];
		if( which.get(idFieldName) == null)
			throw new ResourceException("Entity to be removed is missing an @Id field value.");
		database.get(tableName).set(database.get(tableName).indexOf(findEntryForID(tableName, idFieldName, which.get(idFieldName))), with);
	}

	public void remove(DemoInteractionSpec spec, String tableName, DemoMappedRecord input) throws ResourceException {
		ensureDatabaseExists(tableName);
		if (spec.getKeys().length != 1)
			throw new ResourceException("one and only one @Id field is currently supported.");
		String idFieldName = spec.getKeys()[0].split(" ")[1];
		if( input.get(idFieldName) == null)
			throw new ResourceException("Entity to be removed is missing an @Id field value.");

		database.get(tableName).remove(findEntryForID(tableName, idFieldName, input.get(idFieldName)));
	}

	// IMPORTANT: We could've used DemoSequence instead of nanoTime but I want to emulate a database that creates (auto increment) identity values
	@SuppressWarnings("unchecked")
	private void insertIdentityIfMissing(DemoInteractionSpec spec, DemoMappedRecord mapped) throws ResourceException {
		if (spec.getKeys().length != 1)
			throw new ResourceException("one and only one @Id field is currently supported.");
		long nanoTime = DemoSequence.lastID = System.nanoTime();

		String[] keyTypeAndName = spec.getKeys()[0].split(" ");
		String idFieldType = keyTypeAndName[0];
		String idFieldName = keyTypeAndName[1];
		
		if (mapped.get(idFieldName) == null) {
			if( idFieldType.equalsIgnoreCase("Long"))
				mapped.put(idFieldName, new Long(nanoTime));
			else if( keyTypeAndName[0].equals("String"))
				mapped.put(idFieldName, ""+nanoTime);
			else
				throw new ResourceException("Only Long or String @Id field is currently supported: " + idFieldType);
		}
	}

	public Object findEntryForID( String tableName, String idFieldName, Object id) {
		for (HashMap<String, Object> tableEntry : database.get(tableName)) {
			if( tableEntry.containsKey(idFieldName) && tableEntry.get(idFieldName).equals(id)) {
				return tableEntry;
			}
		}
		return null;
	}
	
	@Override
	public void close() throws ResourceException {
	}

	@Override
	public Interaction createInteraction() throws ResourceException {
		return new DemoInteraction(this);
	}

	@Override
	public LocalTransaction getLocalTransaction() throws ResourceException {
		return transaction;
	}

	@Override
	public ConnectionMetaData getMetaData() throws ResourceException {
		return new DemoConnectionMetaData(this);
	}

	@Override
	public ResultSetInfo getResultSetInfo() throws ResourceException {
		throw ValidationException.operationNotSupported("getResultSetInfo");
	}

	@SuppressWarnings("unchecked")
	public DemoListRecord find(DemoInteractionSpec iSpec, String tableName, DemoMappedRecord searchInput) throws ResourceException {
		ObjectLevelReadQuery readQuery = (ObjectLevelReadQuery) iSpec.getQuery();

		if (readQuery.hasOrderByExpressions()) {
			//for (Expression orderBy : readQuery.getOrderByExpressions()) {
			//}
		}
		
		ensureDatabaseExists(tableName);

		ArrayList<HashMap<String, Object>> filteredTableEntries = (ArrayList<HashMap<String, Object>>) database.get(tableName).clone();

		int findMode=0;
		boolean doCount = doCount(iSpec);
		if (searchInput.size() == 1) {
			findMode = ExpressionOperator.Equal;
			// input only contains one entry, our search key and value
			Map.Entry<String,Object> entry = (Entry<String, Object>) searchInput.entrySet().toArray()[0];
			for (HashMap<String, Object> tableEntry : database.get(tableName)) {
				if( !tableEntry.containsKey(entry.getKey()) || !tableEntry.get(entry.getKey()).equals(entry.getValue())) {
					filteredTableEntries.remove(tableEntry);
				}
			}
		}


		if( findMode == ExpressionOperator.Equal) {
		}
		
		int skip = readQuery.getFirstResult();
		int limit = readQuery.getMaxRows() == 0 ? Integer.MAX_VALUE : readQuery.getMaxRows();

		DemoListRecord results = new DemoListRecord();
		if (doCount) {
			DemoMappedRecord mapped = new DemoMappedRecord();
			mapped.put("count", new Long(filteredTableEntries.size()));
			results.addRecord(mapped);
		} else {
			int n = 0;
			for (HashMap<String, Object> map : filteredTableEntries) {
				if (n++ >= skip && results.size() < limit) {
					DemoMappedRecord mapped = new DemoMappedRecord();
					mapped.putAll(map);
					results.addRecord(mapped);
				}
			}
		}
		return results;
	}

	private boolean doCount(DemoInteractionSpec iSpec) {
		if (iSpec.statement != null) {
			Expression whereExpr = iSpec.statement.getWhereClause();
			if (whereExpr != null && whereExpr.isRelationExpression()) {
				//findMode = handleWhere(ctRecord, query, findMode, whereExpr);
			}
			if (iSpec.statement instanceof SQLSelectStatement) {
				try {
					SQLSelectStatement sss = (SQLSelectStatement) iSpec.statement;
					
					if (sss.getQuery() instanceof ReportQuery) {
						ReportQuery rq = (ReportQuery) sss.getQuery();
						List<ReportItem> items = rq.getItems();
						FunctionExpression fe = (FunctionExpression) items.get(0).getAttributeExpression();
						ExpressionOperator operator = fe.getOperator();
						if (operator.getJavaStrings()[0].equals(".count()")) {
							return true;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}

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

	protected DemoJCAConnectionSpec connectionSpec;
	private DemoTransaction transaction;

	public DemoConnection(DemoJCAConnectionSpec connectionSpec) {
		this.connectionSpec = connectionSpec;
		this.transaction = new DemoTransaction(this);
	}

	// IMPORTANT: We could've used DemoSequence instead nanoTime of but I want to emulate a database that creates (auto increment) identity values
	@SuppressWarnings("unchecked")
	public void write(DemoInteractionSpec spec, String tableName, DemoMappedRecord mapped) throws ResourceException {
		if (!database.containsKey(tableName)) {
			database.put(tableName, new ArrayList<HashMap<String, Object>>());
		}

		if (spec.getKeys().length != 1)
			throw new ResourceException("one and only one @Id field is currently supported.");
		long nanoTime = System.nanoTime(); 
		if (mapped.get(spec.getKeys()[0]) == null) {
			String[] keyTypeAndName = spec.getKeys()[0].split(" ");
			if( keyTypeAndName[0].equals("Long"))
				mapped.put(keyTypeAndName[1], new Long(nanoTime));
			else if( keyTypeAndName[0].equals("String"))
				mapped.put(keyTypeAndName[1], ""+nanoTime);
			else
				throw new ResourceException("Only Long or String @Id field is currently supported.");
		}
		database.get(tableName).add(mapped);
	}

	@SuppressWarnings("unchecked")
	public void update(DemoInteractionSpec spec, String tableName, DemoMappedRecord which, DemoMappedRecord with) throws ResourceException {
		database.get(tableName).set(database.get(tableName).indexOf(which), with);
	}

	public void remove(DemoInteractionSpec spec, String tableName, DemoMappedRecord input) throws ResourceException {
		database.get(tableName).remove(input);
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

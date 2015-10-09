package net.raysforge.eclipselink.hashmap;

import java.lang.reflect.Field;
import java.util.List;

import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.eis.EISPlatform;
import org.eclipse.persistence.eis.interactions.EISInteraction;
import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.expressions.SQLStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sequencing.Sequence;

public class DemoPlatform extends EISPlatform {

	public static String OPERATION = "demo.operation";
	public static String TABLE = "demo.table";
	public static String KEYS = "demo.primaryKeys";
	public static String OPTIONS = "demo.options";
	public static String SKIP = "demo.skip";
	public static String LIMIT = "demo.limit";
	public static String FETCH_SIZE = "demo.fetch";

	public DemoPlatform() {
		super();
		setIsMappedRecordSupported(true);
		setIsIndexedRecordSupported(false);
		setIsDOMRecordSupported(false);
		setSupportsLocalTransactions(true); // left in for demo purposes
		setDefaultNativeSequenceToTable(false);
	}

	@Override
	public boolean supportsIdentity() {
		return true;
	}

	private MappedInteraction getCall(DescriptorQueryManager queryManager, DemoOperation op) {
		MappedInteraction call = new MappedInteraction();
		call.setProperty(DemoPlatform.OPERATION, op);
		call.setProperty(KEYS, getPrimaryKeys(queryManager.getDescriptor()));
		call.setProperty(DemoPlatform.TABLE, ((EISDescriptor) queryManager.getDescriptor()).getDataTypeName());
		if (op == DemoOperation.FIND || op == DemoOperation.REMOVE) {
			for (DatabaseField field : queryManager.getDescriptor().getPrimaryKeyFields()) {
				call.addArgument(field.getName());
			}
		}
		return call;
	}

	/**
	 * THIS IS CALLED FIRST
	 * Allow the platform to initialize the CRUD queries to defaults.
	 * Configure the CRUD operations using GET/PUT and DELETE.
	 */
	@Override
	public void initializeDefaultQueries(DescriptorQueryManager qm, AbstractSession session) {
		if (!qm.hasInsertQuery()) {
			qm.setInsertCall(getCall(qm, DemoOperation.INSERT));
		}
		if (!qm.hasUpdateQuery()) {
			qm.setUpdateCall(getCall(qm, DemoOperation.UPDATE));
		}
		if (!qm.hasReadObjectQuery()) {
			qm.setReadObjectCall(getCall(qm, DemoOperation.FIND));
		}
		if (!qm.hasDeleteQuery()) {
			qm.setDeleteCall(getCall(qm, DemoOperation.REMOVE));
		}
	}

	public String[] getPrimaryKeys(ClassDescriptor cd) {
		if (!cd.hasSimplePrimaryKey()) {
			throw new RuntimeException("only simple primary key is currently supported.");
		}
		List<DatabaseField> primaryKeyFields = cd.getPrimaryKeyFields();
		String[] res = new String[primaryKeyFields.size()];
		int i = 0;
		for (DatabaseField f : primaryKeyFields) {
			try {
				Field field = cd.getJavaClass().getDeclaredField(f.getName()); // f.getTypeName() is null  :-(
				res[i++] = field.getType().getSimpleName() + " " + f.getName();
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}
		return res;
	}

	/*
	 * THIS IS CALLED SECOND
	 */
	public DatasourceCall buildCallFromStatement(SQLStatement statement, DatabaseQuery query, AbstractSession session) {
		//System.out.println("buildCallFromStatement");
		if (!query.isObjectLevelReadQuery()) {
			throw new EISException("Query too complex, only select queries are supported: " + query);
		}

		MappedInteraction interaction = new MappedInteraction();
		// Experimantal
		interaction.setProperty("SQLStatement", statement);

		interaction.setProperty(OPERATION, DemoOperation.FIND);
		interaction.setProperty(TABLE, ((EISDescriptor) query.getDescriptor()).getDataTypeName());
		interaction.setProperty(KEYS, getPrimaryKeys(query.getDescriptor()));

		//interaction.setInputRow(row);
		return interaction;
	}

	/**
	 * THIS IS CALLED THIRD
	 */
	@Override
	public Record createOutputRecord(EISInteraction interaction, AbstractRecord translationRow, EISAccessor accessor) {
		//System.out.println("createOutputRecord");
		if (interaction.getInteractionSpec() != null) {
			boolean a = ((DemoInteractionSpec) interaction.getInteractionSpec()).getOperation() == DemoOperation.UPDATE;
			if (a)
				return (Record) interaction.createRecordElement(interaction.getInputRecordName(), translationRow, accessor);
		} else if (interaction.getProperty(OPERATION) != null) {
			boolean a = interaction.getProperty(OPERATION) == DemoOperation.UPDATE;
			boolean b = interaction.getProperty(OPERATION).equals(DemoOperation.UPDATE.name());
			if (a || b)
				return (Record) interaction.createRecordElement(interaction.getInputRecordName(), translationRow, accessor);
		}
		return null;
	}

	/**
	 * THIS IS CALLED FORTH
	 * Allow the platform to build the interaction spec based on properties defined in the interaction.
	 */
	@Override
	public InteractionSpec buildInteractionSpec(EISInteraction interaction) {
		//System.out.println("buildInteractionSpec");
		return interaction.getInteractionSpec() == null ? new DemoInteractionSpec(interaction) : interaction.getInteractionSpec();
	}

	// Without this you get: net.raysforge.eclipselink.hashmap.DemoPlatform cannot be cast to org.eclipse.persistence.internal.databaseaccess.DatabasePlatform
	private Sequence s;

	public Sequence getSequence(String seqName) {
		if ("SEQ_GEN_IDENTITY".equals(seqName)) {
			if (s == null) {
				s = createPlatformDefaultSequence();
			}
			return s;
		}
		if (seqName == null) {
			return getDefaultSequence();
		} else {
			if (this.sequences != null) {
				return (Sequence) this.sequences.get(seqName);
			} else {
				return null;
			}
		}
	}

	@Override
	protected Sequence createPlatformDefaultSequence() {
		return new DemoSequence();
	}
}

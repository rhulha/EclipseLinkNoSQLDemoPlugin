package net.raysforge.eclipselink.hashmap;

import java.util.Vector;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;

public class DemoSequence extends Sequence {

	public DemoSequence() {
	}

	@Override
	public boolean shouldAcquireValueAfterInsert() {
		return true;
	}

	@Override
	public boolean shouldUseTransaction() {
		return false;
	}

	/*
	@Override
	public boolean shouldAlwaysOverrideExistingValue() {
		return true;
	}
	*/
	
	@Override
	public Object getGeneratedValue(Accessor accessor, AbstractSession writeSession, String seqName) {
		//ClientSession cs = (ClientSession) writeSession;
		//DemoPlatform p = (DemoPlatform) getDatasourcePlatform();
		//System.out.println("getGeneratedValue " + p + " " + seqName);
		return System.currentTimeMillis();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Vector getGeneratedVector(Accessor accessor, AbstractSession writeSession, String seqName, int size) {
		return null;
	}

	@Override
	public void onConnect() {
	}

	@Override
	public void onDisconnect() {
	}

}

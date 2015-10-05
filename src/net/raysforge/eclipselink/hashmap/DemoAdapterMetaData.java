package net.raysforge.eclipselink.hashmap;

import javax.resource.cci.ResourceAdapterMetaData;

public class DemoAdapterMetaData implements ResourceAdapterMetaData {

	@Override
	public String getAdapterName() {
		return "DemoAdapter";
	}

	@Override
	public String getAdapterShortDescription() {
		return "Demo JCA Adapter";
	}

	@Override
	public String getAdapterVendorName() {
		return "raysforge.net";
	}

	@Override
	public String getAdapterVersion() {
		return "2.1.0";
	}

	@Override
	public String[] getInteractionSpecsSupported() {
        String[] specs = new String[2];
        specs[0] = DemoInteractionSpec.class.getName();
        return specs;
	}
	
	@Override
	public String getSpecVersion() {
		return "1.5";
	}

	@Override
	public boolean supportsExecuteWithInputAndOutputRecord() {
		return true;
	}

	@Override
	public boolean supportsExecuteWithInputRecordOnly() {
		return true;
	}

	@Override
	public boolean supportsLocalTransactionDemarcation() {
		return true;
	}

}

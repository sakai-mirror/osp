package org.theospi.portfolio.matrix.control;

import java.util.List;
import java.util.Map;

import org.theospi.portfolio.matrix.model.Scaffolding;

public class MatrixSummaryBean {

	private String raw;
	private Scaffolding scaffolding;
	private Boolean shouldTransform = Boolean.FALSE;
	
	private String scaffoldingId;
	private String jobId;
	
	public String getRaw() {
		return raw;
	}
	
	public void setRaw(String raw) {
		this.raw = raw;
	}
	
	public Scaffolding getScaffolding() {
		return scaffolding;
	}
	
	public void setScaffolding(Scaffolding scaffolding) {
		this.scaffolding = scaffolding;
	}
		
	public Boolean getShouldTransform() {
		return shouldTransform;
	}
	
	public void setShouldTransform(Boolean shouldTransform) {
		this.shouldTransform = shouldTransform;
	}

	public String getScaffoldingId() {
		return scaffoldingId;
	}

	public void setScaffoldingId(String scaffoldingId) {
		this.scaffoldingId = scaffoldingId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
}

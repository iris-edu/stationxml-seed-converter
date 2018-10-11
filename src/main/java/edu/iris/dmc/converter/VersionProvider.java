package edu.iris.dmc.converter;

public class VersionProvider {

	private String version;
	private String buildTime;

	public VersionProvider(String version, String buildTime) {
		this.version = version;
		this.buildTime = buildTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBuildTime() {
		return buildTime;
	}

	public void setBuildTime(String buildTime) {
		this.buildTime = buildTime;
	}

}

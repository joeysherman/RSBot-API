package org.powerbot.script.methods;

import java.util.Properties;

public class Environment extends ClientLink {//TODO remove this class entirely
	private static final Properties properties = new Properties();

	public Environment(ClientFactory factory) {
		super(factory);
	}

	public static Properties getProperties() {
		return properties;
	}

	public static String getDisplayName() {
		return properties.getProperty("user.name");
	}

	public static int getUserId() {
		final String s = properties.getProperty("user.id");
		return s == null || s.isEmpty() ? -1 : Integer.parseInt(s);
	}
}

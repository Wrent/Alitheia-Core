package eu.sqooss.impl.service.webadmin;

import java.util.ArrayList;
import java.util.List;

import eu.sqooss.service.util.StringUtils;

public class LogsView extends AbstractView {

	/**
	 * Creates an HTML safe list of the logs
	 * 
	 * @return List with HTML safe log strings
	 */
	public static List<String> getLogs() {
		String[] names = sobjLogManager.getRecentEntries();
		ArrayList<String> logs = new ArrayList<String>();
		if (names != null) {
			for (String name : names) {
				logs.add(StringUtils.makeXHTMLSafe(name));
			}
		}
		return logs;
	}

}

package uk.co.wansdykehouse.versionchecker;

import java.util.ArrayList;
import java.util.List;

public class CheckResult {

	private boolean isUpgradeAvailable;
	
	private List<String> messages = new ArrayList<>();
	
	protected CheckResult() {
	}
	
	protected CheckResult add(String message) {
		messages.add(message);
		
		return this;
	}
	
	protected CheckResult setUpgradeAvailable(boolean value) {
		this.isUpgradeAvailable = value;
		
		return this;
	}
	
	protected CheckResult setUpgradeAvailable() {
		this.isUpgradeAvailable = true;
		
		return this;
	}
	
	public boolean isUpgradeAvailable() {
		return this.isUpgradeAvailable;
	}
	
	public List<String> getMessages() {
		return messages;
	}
}

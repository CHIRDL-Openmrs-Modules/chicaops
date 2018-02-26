/**
 * 
 */
package org.openmrs.module.chicaops.dashboard;


/**
 * Used to show a unique rule token name to rule type combination.
 * 
 * @author Steve McKee
 */
public class RuleIdentifier {
	private String tokenName;
	private String ruleType;
	
	/**
	 * Constructor method
	 * 
	 * @param tokenName The rule token name
	 * @param ruleType The rule type
	 */
	public RuleIdentifier (String tokenName, String ruleType) {
		this.tokenName = tokenName;
		this.ruleType = ruleType;
	}
	
	/**
	 * @return the tokenName
	 */
	public String getTokenName() {
		return tokenName;
	}
	
	/**
	 * @param tokenName the tokenName to set
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
	
	/**
	 * @return the ruleType
	 */
	public String getRuleType() {
		return ruleType;
	}
	
	/**
	 * @param ruleType the ruleType to set
	 */
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ruleType == null) ? 0 : ruleType.hashCode());
		result = prime * result + ((tokenName == null) ? 0 : tokenName.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleIdentifier other = (RuleIdentifier) obj;
		if (ruleType == null) {
			if (other.ruleType != null)
				return false;
		} else if (!ruleType.equals(other.ruleType))
			return false;
		if (tokenName == null) {
			if (other.tokenName != null)
				return false;
		} else if (!tokenName.equals(other.tokenName))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RuleIdentifier [tokenName=" + tokenName + ", ruleType=" + ruleType + "]";
	}
}

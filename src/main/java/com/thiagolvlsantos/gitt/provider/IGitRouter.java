package com.thiagolvlsantos.gitt.provider;

/**
 * A router for git based on method annotation and its actual parameters.
 * 
 * @author thiagolvlsantos@gmail.com
 *
 */
public interface IGitRouter {

	/**
	 * Separator when a qualifier is set.
	 */
	String SEPARATOR = "_";

	/**
	 * Returns the group according to arguments.
	 * 
	 * @param group The original group.
	 * @param args  The method arguments captured by Aspects.
	 * @return The group reference.
	 */
	default String qualifier(String group, Object[] args) {
		return "";
	}
}
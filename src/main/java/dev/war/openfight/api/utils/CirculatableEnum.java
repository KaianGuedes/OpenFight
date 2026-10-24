package dev.war.openfight.api.utils;

public interface CirculatableEnum<T extends Enum<T>> {
	/**
	 * Return a next enum
	 * @return
	 */
	public T nextEnum();
}

package org.modelsphere.jack.srtool.features.layout;

import java.util.HashMap;

/**
 * The Class LayoutAttribute.
 * 
 * @param <T>
 *            the generic type
 */
public class LayoutAttribute<T> {

    /** The key. */
    private String key;

    /** The default value. */
    private T defaultValue;

    /**
     * Instantiates a new layout attribute.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     */
    public LayoutAttribute(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the default value.
     * 
     * @return the default value
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the key.
     * 
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the value.
     * 
     * @param attributes
     *            the attributes
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public T getValue(HashMap<LayoutAttribute<?>, Object> attributes) {
        T value = null;
        try {
            value = (T) attributes.get(this);
        } catch (Exception e) {
        }
        return value == null ? defaultValue : value;
    }

    /**
     * Sets the value.
     * 
     * @param attributes
     *            the attributes
     * @param value
     *            the value
     */
    public void setValue(HashMap<LayoutAttribute<?>, Object> attributes, T value) {
        attributes.put(this, value);
    }
}

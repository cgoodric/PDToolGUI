package com.compositesw.common;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by doug
 * Date: Aug 2, 2010
 */
public abstract class ResourceBundleFactory {
    public ResourceBundleFactory() { }

    public abstract ResourceBundle getBundle(Locale locale);

}

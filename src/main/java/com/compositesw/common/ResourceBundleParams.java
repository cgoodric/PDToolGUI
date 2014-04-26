/**
 * Copyright 2003 Composite Software, Inc. All Rights Reserved.
 */
package com.compositesw.common;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleParams extends ResourceBundleFactory
{
  private String baseName;
  private ClassLoader loader;
  private String domain;

  public ResourceBundleParams(String baseName, ClassLoader loader)
  {
    this.baseName = baseName;
    this.loader = loader;
  }

  public ResourceBundleParams(String baseName,
                              ClassLoader loader,
                              String domain)
  {
    this.baseName = baseName;
    this.loader = loader;
    this.domain = domain;
  }

  public String getBaseName() {
    return baseName;
  }

  public ClassLoader getClassLoader() {
    return loader;
  }

  public String getDomain() {
    return domain;
  }

  /*
  public String getMessage(int errorCode, Object[] args) {
    // Remind: implement this
    return null;
  }
  */

  public CompositeResourceBundle getBundle(Locale locale) {
    if (locale == null)
      locale = Locale.getDefault();
    CompositeResourceBundle bundle = (CompositeResourceBundle)
      ResourceBundle.getBundle(baseName, locale, loader);
    bundle.setDomain(domain);
    return bundle;
  }
}

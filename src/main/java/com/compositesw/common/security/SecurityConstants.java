/*
 * @(#)$RCSfile: SecurityConstants.java,v $
 * 
 * Copyright (c) 2002 Composite Software, Inc. 2988 Campus Drive, San Mateo,
 * California, 94403, U.S.A. All rights reserved.
 */

package com.compositesw.common.security;

import com.compositesw.common.repository.MetadataConstants;

public interface SecurityConstants {

    String DOMAIN = "security";
    int MODULE_ID = 1003;
    String MODULE_NAME = "security";

    // Ids of built-in users.
    int USER_ID_INVALID = -1;
    int USER_ID_ADMIN = -1973;
    int USER_ID_SYSTEM = -1974;
    int USER_ID_NOBODY = -1975;
    int USER_ID_ANONYMOUS = -1976;
    int USER_ID_UNKNOWN = -1977;
    int USER_ID_MONITOR = -1978;

    // Names of built-in users.
    String USER_NAME_ADMIN = "admin";
    String USER_NAME_SYSTEM = "system";
    String USER_NAME_NOBODY = "nobody";
    String USER_NAME_ANONYMOUS = "anonymous";
    String USER_NAME_UNKNOWN = "unknown";
    String USER_NAME_MONITOR = "monitor";

    // Ids of built-in groups.
    int GROUP_ID_INVALID = -1;
    int GROUP_ID_ADMIN = 1;
    int GROUP_ID_ALL = 2;
    int GROUP_ID_DYNAMIC_ALL = 3;

    // Names of built-in groups.
    String GROUP_NAME_ADMIN = "admin";
    String GROUP_NAME_ALL = "all";
    String GROUP_NAME_DYNAMIC_ALL = "all";

    // Domain categories.
    short DOMAIN_CATEGORY_COMPOSITE = MetadataConstants.DOMAIN_CATEGORY_COMPOSITE;
    short DOMAIN_CATEGORY_DYNAMIC = MetadataConstants.DOMAIN_CATEGORY_DYNAMIC;
    short DOMAIN_CATEGORY_EXTERNAL = MetadataConstants.DOMAIN_CATEGORY_EXTERNAL;

    // Ids of built-in domains.
    int DOMAIN_ID_COMPOSITE = 11;
    int DOMAIN_ID_DYNAMIC = 12;

    // Names of built-in domains.
    String DOMAIN_NAME_COMPOSITE = "composite";
    String DOMAIN_NAME_DYNAMIC = "dynamic";

    // Domain type names.
    String DOMAIN_TYPE_NAME_COMPOSITE = "COMPOSITE";
    String DOMAIN_TYPE_NAME_DYNAMIC = "DYNAMIC";
    String DOMAIN_TYPE_NAME_LDAP = "LDAP";

    // Domain type annotations.
    String DOMAIN_TYPE_ANNOTATION_COMPOSITE = "Composite authentication domain";
    String DOMAIN_TYPE_ANNOTATION_DYNAMIC = "Dynamic authentication domain";
    String DOMAIN_TYPE_ANNOTATION_LDAP = "LDAP authentication domain";

    String DOMAIN_TYPE_NAME = "a_domain_external_type";

    // Domain attribute keys - used only by webapi & studio.
    String DOMAIN_NAME = "a_domain_name";
    String DOMAIN_TYPE = "a_domain_type";

    String TEST_IDENTITY = "test_identity";
    String TEST_USER = "test_user";
    String TEST_GROUPS = "test_groups";
}
/*-
 * ===============LICENSE_START================================================
 * Acumos Apache-2.0
 * ============================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ============================================================================
 * This Acumos software file is distributed by Nordix Foundation
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */

package org.acumos.licensemanager.profilevalidator.resource;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.acumos.licensemanager.profilevalidator.model.SchemaMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** LicenseJsonSchema class. */
public final class LicenseJsonSchema {

  /** Logger for any exception handling. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /** LicenceProfile schema version map */
  private static Map<String, String> schemaVersionToUrlMap =
      new HashMap<String, String>(
          Map.ofEntries(
              new AbstractMap.SimpleEntry<String, String>(
                  "1.0.0", "/schema/1.0.0/license-profile.json"),
              new AbstractMap.SimpleEntry<String, String>(
                  "boreas", "/schema/boreas/license-profile.json")));

  /** JsonSchema object for validation of license schema. */
  private static JsonSchema jsonSchema;

  private static Pattern schemaRegexPattern;

  /** Do not instantiate. */
  private LicenseJsonSchema() {}

  /** Schema URL scheme/pattern http://<HOST>/<SUB_PATH>/schema/<VERSION>/license-profile.json */
  public static SchemaMetadata getSchemaMetadata(String schemaUrl) {
    if (schemaRegexPattern == null) {
      schemaRegexPattern = Pattern.compile("(.*)\\/schema\\/(.*)\\/(.*)");
    }
    Matcher m = schemaRegexPattern.matcher(schemaUrl);
    SchemaMetadata metadata = null;
    if (m.find()) {
      metadata = new SchemaMetadata(m.group(0), m.group(1), m.group(2), m.group(3));
    }
    // System.out.println("schema metadata ---- " + metadata);
    return metadata;
  }

  /** */
  private static String getSchemaUrl(String schemaVersion) {
    return LicenseJsonSchema.schemaVersionToUrlMap.get(schemaVersion);
  }

  /**
   * Get the license json schema as JsonSchema.
   *
   * @return a {@link com.networknt.schema.JsonSchema} object.
   * @throws java.io.IOException if any.
   */
  public static JsonSchema getSchema(String schemaVersion) throws IOException {
    if (jsonSchema != null) {
      return jsonSchema;
    }
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance();
    String schemaUrlPath = LicenseJsonSchema.getSchemaUrl(schemaVersion);
    if (schemaUrlPath == null) {
      throw new IOException("LicenseJsonSchema: Non-supported version " + schemaVersion);
    }
    URL schemaUrl = LicenseJsonSchema.class.getResource(schemaUrlPath);
    try (InputStream is = schemaUrl.openStream()) {
      jsonSchema = factory.getSchema(is);
    } catch (IOException e) {
      LOGGER.error("unable to process license schema {}", e);
    }
    return jsonSchema;
  }
}

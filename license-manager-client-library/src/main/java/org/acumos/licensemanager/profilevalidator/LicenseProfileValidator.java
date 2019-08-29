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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */

package org.acumos.licensemanager.profilevalidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.acumos.licensemanager.profilevalidator.exceptions.LicenseJsonException;
import org.acumos.licensemanager.profilevalidator.model.ILicenseProfileValidator;
import org.acumos.licensemanager.profilevalidator.model.LicenseProfileValidationResults;
import org.acumos.licensemanager.profilevalidator.resource.LicenseJsonSchema;

/**
 * LicenseProfileValidator will verify the license.json to ensure there are no json errors or json
 * schema errors
 */
public class LicenseProfileValidator implements ILicenseProfileValidator {

  private ObjectMapper objectMappper;

  public LicenseProfileValidator() {}

  public LicenseProfileValidator(ObjectMapper mapper) {
    objectMappper = mapper;
  }

  private ObjectMapper getObjectMapper() {
    if (objectMappper == null) {
      objectMappper = new ObjectMapper();
    }
    return objectMappper;
  }

  @Override
  public LicenseProfileValidationResults validate(String jsonString) throws LicenseJsonException {
    InputStream targetStream = new ByteArrayInputStream(jsonString.getBytes());
    LicenseProfileValidationResults results = null;
    try {
      results = validate(targetStream);
    } finally {
      try {
        targetStream.close();
      } catch (IOException e) {
        throw new LicenseJsonException("LicenseProfileJson: could not convert string to bytes", e);
      }
    }
    return results;
  }

  @Override
  public final LicenseProfileValidationResults validate(InputStream inputStream)
      throws LicenseJsonException {
    // read in json schema
    JsonNode node;
    try {
      node = getObjectMapper().readTree(inputStream);
    } catch (Exception e) {
      throw new LicenseJsonException("LicenseProfileJson: issue reading input", e);
    }
    return validate(node);
  }

  public LicenseProfileValidationResults validate(final JsonNode node) throws LicenseJsonException {
    JsonSchema schema;
    try {
      schema = LicenseJsonSchema.getSchema();
    } catch (IOException e) {
      throw new LicenseJsonException("LicenseProfileJson: could not load schema", e);
    }

    if (node == null) {
      throw new LicenseJsonException("LicenseProfileJson: could not load json");
    }
    Set<ValidationMessage> errors = schema.validate(node);
    LicenseProfileValidationResults results = new LicenseProfileValidationResults();
    results.setJsonSchemaErrors(errors);
    return results;
  }
}

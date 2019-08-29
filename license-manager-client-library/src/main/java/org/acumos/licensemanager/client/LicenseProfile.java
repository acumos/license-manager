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
 **/

package org.acumos.licensemanager.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.jsonvalidator.exceptions.LicenseJsonException;
import org.acumos.licensemanager.jsonvalidator.exceptions.LicenseProfileException;
import org.acumos.licensemanager.jsonvalidator.model.ILicenseProfileValidator;
import org.acumos.licensemanager.jsonvalidator.model.LicenseJsonValidator;
import org.acumos.licensemanager.jsonvalidator.model.LicenseProfileValidationResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

/**
 * LicenseProfile api provides - list of available license profile templates and a specific template
 * - validation of given license profile
 *
 * <p>This API requires CDS 3.0.0 and above.
 */
public class LicenseProfile implements ILicenseProfileValidator {

  /** Logger for any exceptions that happen while creating a RTU with CDS. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  /** dataClient must be provided by consumer of this library. */
  private final ICommonDataServiceRestClient dataClient;

  private final LicenseJsonValidator licenseJsonValidator;

  /**
   * The implementation of the CDS is required to enable this library.
   *
   * @param dataServiceClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   */
  public LicenseProfile(final ICommonDataServiceRestClient dataServiceClient) {
    this.dataClient = dataServiceClient;
    this.licenseJsonValidator = new LicenseJsonValidator();
  }

  /**
   * Fetches and returns list of available license profile templates.
   *
   * @see #{@link org.acumos.cds.client.ICommonDataServiceRestClient#getLicenseProfileTemplates}
   * @return list of license profile templates, which may be empty
   */
  public final List<MLPLicenseProfileTemplate> getTemplates() throws LicenseProfileException {

    List<MLPLicenseProfileTemplate> templates;
    try {
      // sort by priority
      // show all records
      Map<String, String> fieldToDirectionMap = new HashMap<String, String>();
      fieldToDirectionMap.put("priority", "DESC");
      RestPageRequest req = new RestPageRequest(0, 0, fieldToDirectionMap);
      RestPageResponse<MLPLicenseProfileTemplate> templatesFromCDS =
          dataClient.getLicenseProfileTemplates(req);
      templates = templatesFromCDS.getContent();
    } catch (RestClientResponseException ex) {
      LOGGER.error("getTemplates failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseProfileException("getTemplates failed", ex);
    }
    return templates;
  }

  /**
   * Gets the license profile template contents based on the given template Id
   *
   * @see #{@link org.acumos.cds.client.ICommonDataServiceRestClient#getLicenseProfileTemplate}
   * @param templateId
   * @return the license profile template for the given template Id
   * @throws LicenseProfileException
   */
  public final MLPLicenseProfileTemplate getTemplate(long templateId)
      throws LicenseProfileException {

    MLPLicenseProfileTemplate licenseTemplate;
    try {
      licenseTemplate = dataClient.getLicenseProfileTemplate(templateId);
    } catch (RestClientResponseException ex) {
      LOGGER.error("getTemplate failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseProfileException("getTemplate failed", ex);
    }
    return licenseTemplate;
  }

  @Override
  public LicenseProfileValidationResults validate(String jsonString) throws LicenseJsonException {
    return licenseJsonValidator.validate(jsonString);
  }

  @Override
  public LicenseProfileValidationResults validate(JsonNode node) throws LicenseJsonException {
    return licenseJsonValidator.validate(node);
  }

  @Override
  public LicenseProfileValidationResults validate(InputStream input) throws LicenseJsonException {
    return licenseJsonValidator.validate(input);
  }
}

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

package org.acumos.licensemanager.client.model;

import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import org.acumos.lum.handler.ApiException;
import org.acumos.lum.model.PutAssetUsageSuccessResponse;

/** Verified that there is a right to use for specified userId and solutionID. */
public class LicenseRtuVerification implements ILicenseRtuVerification {

  /** List of allowed usages. */
  private Map<LicenseAction, Boolean> allowedToUse =
      new EnumMap<LicenseAction, Boolean>(LicenseAction.class);

  private ApiException apiException;
  private PutAssetUsageSuccessResponse lumResponse;

  /**
   * Add {@link org.acumos.licensemanager.client.model.LicenseAction} to be verified.
   *
   * @param action a {@link org.acumos.licensemanager.client.model.LicenseAction} object.
   * @param allowed a boolean.
   */
  public final void addAction(final LicenseAction action, final boolean allowed) {
    allowedToUse.put(action, allowed);
  }

  @Override
  public final Map<LicenseAction, Boolean> getAllowedToUse() {
    return ImmutableMap.copyOf(allowedToUse);
  }

  @Override
  public final boolean isAllowed(final LicenseAction action) {
    if (allowedToUse.get(action) == null) {
      return false;
    }
    return allowedToUse.get(action).booleanValue();
  }

  /** @param responseException if there is an LUM api exception capture that here */
  public void setApiException(ApiException responseException) {
    apiException = responseException;
  }

  /** @return the apiException LUM api exception object */
  public ApiException getApiException() {
    return apiException;
  }

  /** @param response original response from LUM */
  public void setLumResponse(PutAssetUsageSuccessResponse response) {
    lumResponse = response;
  }

  /** @return the lumResponse */
  public PutAssetUsageSuccessResponse getLumResponse() {
    return lumResponse;
  }
}

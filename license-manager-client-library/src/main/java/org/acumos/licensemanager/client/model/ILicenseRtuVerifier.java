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

import java.util.concurrent.CompletableFuture;
import org.acumos.licensemanager.exceptions.RightToUseException;

/** An object that verifies the license by using the right to use authority. */
public interface ILicenseRtuVerifier {

  /**
   * Checks for siteWide RTU for a solutionId If no siteWide RTU exists check for userId.
   *
   * @param licenseDownloadRequest a {@link
   *     org.acumos.licensemanager.client.model.VerifyLicenseRequest} object.
   * @return a {@link org.acumos.licensemanager.client.model.ILicenseRtuVerification} object.
   * @throws RightToUseException if any.
   */
  CompletableFuture<LicenseRtuVerification> verifyRtu(VerifyLicenseRequest licenseDownloadRequest)
      throws RightToUseException;
}

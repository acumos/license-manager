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

import java.lang.invoke.MethodHandles;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LicenseCreator Library will create a right to use for either a solution and can be added for the
 * entire site or for a specific user.
 */
// TODO implements ILicenseProfile
public class LicenseProfile {
  /** Logger for any exceptions that happen while creating a RTU with CDS. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  /** dataClient must be provided by consumer of this library. */
  private final ICommonDataServiceRestClient dataClient;

  /**
   * The implementation of the CDS is required to enable this library.
   *
   * @param dataServiceClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   */
  public LicenseProfile(final ICommonDataServiceRestClient dataServiceClient) {
    this.dataClient = dataServiceClient;
  }

  public final String[] getDefaultLicenses() throws RightToUseException {
    throw new UnsupportedOperationException("TODO");
  }

  public final String getDefaultLicense(String licenseKey) {
    throw new UnsupportedOperationException("TODO");
  }
}

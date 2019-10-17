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

package org.acumos.licensemanager.client.main;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.acumos.licensemanager.client.model.LicenseRtuVerification;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
import org.acumos.licensemanager.client.rtu.LicenseRtuVerifier;

/**
 * License Verify Main program. Input to main program: String solutionId String userId
 *
 * <p>Envirionment variables required to point to CCDS api ACUMOS_CDS_HOST ACUMOS_CDS_PORT
 * ACUMOS_CDS_USER ACUMOS_CDS_PASSWORD
 */
public class LicenseRtuVerifierMain {

  /** Common data service host name. Set as an environment variable ACUMOS_CDS_HOST. */
  private static final String LUM_HOSTNAME = System.getenv("LUM_HOST");

  /** Common data context path. */
  private static final String LUM_CONTEXT_PATH = "";
  /**
   * Common data service port -- may require NodePort setup if using K8. Set as an environment
   * variable ACUMOS_CDS_PORT.
   */
  private static final int LUM_PORT = Integer.parseInt(System.getenv("LUM_PORT"));

  /** No not allow for utility class from being instantiated. */
  protected LicenseRtuVerifierMain() {
    // prevents calls from subclass
    throw new UnsupportedOperationException();
  }

  /**
   * String solutionId String userId.
   *
   * @param args an array of {@link java.lang.String} objects.
   * @throws java.lang.Exception if any.
   */
  public static void main(final String[] args) throws Exception {

    URL lumServer = new URL("http", LUM_HOSTNAME, LUM_PORT, LUM_CONTEXT_PATH);

    LicenseRtuVerifier licenseVerifier = new LicenseRtuVerifier(lumServer.toExternalForm());
    VerifyLicenseRequest verifyRequest =
        new VerifyLicenseRequest(
            LicenseAction.valueOf(args[3]),
            args[0],
            args[1],
            args[2],
            UUID.randomUUID().toString());
    CompletableFuture<LicenseRtuVerification> licenseResPromise =
        licenseVerifier.verifyRtu(verifyRequest);
    LicenseRtuVerification licenseRes = licenseResPromise.get();
    System.out.println("Verified rtu");
    if (licenseRes.getApiException() != null) {
      System.err.println(licenseRes.getApiException().getMessage());
    }
    System.out.println(args[3] + " allowed? " + licenseRes.isAllowed());

    System.exit(0);
  }
}

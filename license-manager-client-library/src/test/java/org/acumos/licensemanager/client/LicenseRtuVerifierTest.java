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

package org.acumos.licensemanager.client;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.junit.Assert.assertTrue;

import io.specto.hoverfly.junit.rule.HoverflyRule;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.acumos.licensemanager.client.model.LicenseRtuVerification;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
import org.acumos.licensemanager.client.rtu.LicenseRtuVerifier;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * License Manager Client Unit tests - Test creation of the RTU and RTU references - Test reading
 * and verifying license/right to use exists.
 */
public class LicenseRtuVerifierTest {

  private static final String LUM_SERVER = "http://my-lum-server.com";
  private static final String SOLUTION_ID = UUID.randomUUID().toString();
  private static final String REVISION_ID = UUID.randomUUID().toString();
  private static final String ASSET_USAGE_ID = UUID.randomUUID().toString();

  // private class MockDatabaseClient extends CommonDataServiceRestClientMockImpl {}

  @ClassRule
  public static HoverflyRule hoverflyRule =
      HoverflyRule.inSimulationMode(
          dsl(
              service(LUM_SERVER)
                  .put("/api/v1/asset-usage/" + ASSET_USAGE_ID)
                  .anyBody()
                  .willReturn(success("{ \"usageEntitled\": true }", "application/json"))));

  @Test
  public void licenseVerifyDownloadUse()
      throws InterruptedException, ExecutionException, RightToUseException {

    boolean rtuFlag;
    LicenseAction licenseAction = LicenseAction.DOWNLOAD;
    String loggedInUserId = "test";

    VerifyLicenseRequest licenseDownloadRequest =
        new VerifyLicenseRequest(
            licenseAction, SOLUTION_ID, REVISION_ID, loggedInUserId, ASSET_USAGE_ID);
    licenseDownloadRequest.setAction(licenseAction);
    LicenseRtuVerifier licenseVerifier = new LicenseRtuVerifier(LUM_SERVER);
    CompletableFuture<LicenseRtuVerification> verifyUserRTU =
        licenseVerifier.verifyRtu(licenseDownloadRequest);

    LicenseRtuVerification verification = verifyUserRTU.get();
    // returns true or false if rtu exists
    rtuFlag = verification.isAllowed(licenseAction);
    assertTrue("download is allowed", rtuFlag);
  }
}

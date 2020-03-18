/*-
 * ===============LICENSE_START================================================
 * Acumos Apache-2.0
 * ============================================================================
 * Copyright (C) 2020 Tech M.
 * ============================================================================
 * This Acumos software file is distributed by Tech M
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

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.licensemanager.client.model.RegisterAssetRequest;
import org.acumos.licensemanager.client.rtu.LicenseAsset;
import org.acumos.lum.model.GetEntitledSwidTagsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * GetEntitledSwidTag by user Main program. Input to main program: String userId String action
 *
 * <p>Envirionment variables required to point to CCDS api ACUMOS_CDS_HOST ACUMOS_CDS_PORT
 * ACUMOS_CDS_USER ACUMOS_CDS_PASSWORD
 */
public class GetEntitledSwidTagsByUserMain {

  /** Logger for any exception handling. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /** Common data service host name. Set as an environment variable ACUMOS_CDS_HOST. */
  private static final String CDS_HOSTNAME = System.getenv("ACUMOS_CDS_HOST");

  /** Common data context path. */
  private static final String CDS_CONTEXT_PATH = "/ccds";
  /**
   * Common data service port -- may require NodePort setup if using K8. Set as an environment
   * variable ACUMOS_CDS_PORT.
   */
  private static final int CDS_PORT = Integer.parseInt(System.getenv("ACUMOS_CDS_PORT"));

  /** Common data service host name. Set as an environment variable ACUMOS_CDS_HOST. */
  private static final String LUM_HOSTNAME = System.getenv("LUM_HOST");

  /** Common data context path. */
  private static final String LUM_CONTEXT_PATH = "";
  /**
   * Common data service port -- may require NodePort setup if using K8. Set as an environment
   * variable ACUMOS_CDS_PORT.
   */
  private static final int LUM_PORT = Integer.parseInt(System.getenv("LUM_PORT"));

  /** Common data service user name. Set as an environment variable ACUMOS_CDS_USER. */
  private static final String USER_NAME = System.getenv("ACUMOS_CDS_USER");

  /** Common data service password. Set as an environment variable ACUMOS_CDS_PASSWORD. */
  private static final String PASSWORD = System.getenv("ACUMOS_CDS_PASSWORD");

  private static final String NEXUS_HOSTNAME = System.getenv("NEXUS_HOSTNAME");

  private static final int NEXUS_PORT = Integer.parseInt(System.getenv("NEXUS_PORT"));

  private static final String NEXUS_REPO_PATH = System.getenv("NEXUS_REPO_PATH");

  /** No not allow for utility class from being instantiated. */
  protected GetEntitledSwidTagsByUserMain() {
    // prevents calls from subclass
    throw new UnsupportedOperationException();
  }

  /**
   * Main program can be used with the following arguments requires position order.
   * GetEntitledSwidTagsMain userId action
   *
   * @param args an array of {@link java.lang.String} objects.
   * @throws java.lang.Exception if any.
   */
  public static void main(final String[] args) throws Exception {

    URL url = new URL("http", CDS_HOSTNAME, CDS_PORT, CDS_CONTEXT_PATH);
    URL lumServer = new URL("http", LUM_HOSTNAME, LUM_PORT, LUM_CONTEXT_PATH);

    URL nexusServer = new URL("http", NEXUS_HOSTNAME, NEXUS_PORT, NEXUS_REPO_PATH);
    LOGGER.info("createClient: URL is {}", url);
    ICommonDataServiceRestClient client =
        CommonDataServiceRestClientImpl.getInstance(url.toString(), USER_NAME, PASSWORD);

    try {

      final LicenseAsset licenseAsset =
          new LicenseAsset(client, lumServer.toExternalForm(), nexusServer.toExternalForm());
      RegisterAssetRequest registerAsset = new RegisterAssetRequest();

      String userId = args[0];
      String action = args[1];

      CompletableFuture<GetEntitledSwidTagsResponse> getEntitledSwidTagsFuture =
          licenseAsset.getEntitledSwidTagsByUser(userId, action);

      GetEntitledSwidTagsResponse response = getEntitledSwidTagsFuture.get();

      System.out.println("FetchedSwid" + response);

    } catch (HttpStatusCodeException ex) {
      LOGGER.error("basicSequenceDemo failed, server reports: {}", ex.getResponseBodyAsString());
      throw ex;
    }
    System.exit(0);
  }
}

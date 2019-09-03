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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.licensemanager.client.model.RegisterAssetRequest;
import org.acumos.licensemanager.client.model.RegisterAssetResponse;
import org.acumos.licensemanager.exceptions.LicenseAssetRegistrationException;
import org.acumos.lum.handler.ApiCallback;
import org.acumos.lum.handler.ApiException;
import org.acumos.lum.handler.SwidTagApi;
import org.acumos.lum.model.BaseRequestTop;
import org.acumos.lum.model.LicenseProfile;
import org.acumos.lum.model.PutSwidTagRequest;
import org.acumos.lum.model.PutSwidTagResponse;
import org.acumos.lum.model.SWIDBody;
import org.acumos.lum.model.SWIDBodySwCatalogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;
import org.threeten.bp.OffsetDateTime;

/**
 * License Asset library creates the asset definition in LUM to allow ODRL based RTU file to target
 * the asset or asset collection (category, catalog of the asset)
 *
 * <ul>
 *   <li>This api requires the location of the LUM API server as a URL
 *   <li>This api also requires the CDS service for lookups of the required asset targeting info
 * </ul>
 *
 * It is expected that this api is called on each revision of the solution/asset.
 */
public class LicenseAsset {
  /** Logger for any exceptions that happen while creating a RTU with CDS. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  /** dataClient must be provided by consumer of this library. */
  private final ICommonDataServiceRestClient dataClient;

  private final String lumServer;

  /** The implementation of the CDS is required to enable this library. */
  public LicenseAsset(
      final ICommonDataServiceRestClient dataServiceClient, final String lumServer) {
    this.dataClient = dataServiceClient;
    this.lumServer = lumServer;
  }

  /**
   * When Acumos has had a security verification scan the model is ready to be registered in LUM To
   * allow an agreement to be added to LUM we need the asset to be registered
   *
   * <p>1. converts {@link RegisterAssetRequest} to {@link PutSwidTagRequest} required fields to
   * start with There needs to be some boiler plate to fill in {@link BaseRequestTop} - needed
   * before request is sent 2. {@link UUID#randomUUID()} to get requestId 3. {@link
   * org.threeten.bp.OffsetDateTime#now()} for requested time uses {@link
   * SwidTagApi#putSwidTagAsync(String, PutSwidTagRequest, org.acumos.lum.handler.ApiCallback)}
   *
   * @return IRegisterAssetResponse
   * @throws LicenseRegistrationException
   */
  public final CompletableFuture<RegisterAssetResponse> register(RegisterAssetRequest request)
      throws LicenseAssetRegistrationException {

    if (request == null) {
      throw new IllegalArgumentException("request is not defined");
    }
    if (request.getSolutionId() == null) {
      throw new IllegalArgumentException("request solution id is not defined");
    }
    if (request.getRevisionId() == null) {
      throw new IllegalArgumentException("request revisionId is not defined");
    }
    CompletableFuture<RegisterAssetResponse> completableFuture =
        new CompletableFuture<RegisterAssetResponse>();

    // api setup
    SwidTagApi swidTag = new SwidTagApi();
    swidTag.getApiClient().setBasePath(lumServer);

    // Prepare request object -- standard for all interaction with LUM
    PutSwidTagRequest putSwidTagRequest = new PutSwidTagRequest();
    putSwidTagRequest.setRequestId(UUID.randomUUID());
    putSwidTagRequest.setRequested(OffsetDateTime.now());

    // TODO SWIDBodySwidTagDetails -- edition, revision again, url for software
    // TODO toolkit type?

    String swTagId = request.getSolutionId() + ":" + request.getRevisionId();
    SWIDBody swidBody = new SWIDBody();
    swidBody.setSwPersistentId(request.getSolutionId());

    swidBody.setSwTagId(swTagId);

    // CDS based calls --- if data is not provided make calls to get category and version number
    addCDSDataToSwidBody(request, swidBody);

    // add catalogs to swidbody
    addCatalogsToSwidBody(request, swidBody);

    // LicenseProfile profile = new LicenseProfile();
    // putSwidTagRequest.setLicenseProfile(licenseProfile);
    // RequestTopUser userId
    LicenseProfile profile = new LicenseProfile();
    profile.isRtuRequired(request.isRtuRequired());
    profile.setLicenseName(request.getLicenseName());
    putSwidTagRequest.setLicenseProfile(profile);
    try {
      swidTag.putSwidTagAsync(
          swTagId,
          putSwidTagRequest,
          new ApiCallback<PutSwidTagResponse>() {

            @Override
            public void onSuccess(
                PutSwidTagResponse arg0, int arg1, Map<String, List<String>> arg2) {
              RegisterAssetResponse resp = new RegisterAssetResponse();
              // resp.setRawResponse(arg0);
              completableFuture.complete(resp);
            }

            @Override
            public void onFailure(ApiException arg0, int arg1, Map<String, List<String>> arg2) {
              completableFuture.completeExceptionally(arg0);
            }

            // TODO move this to override class to prevent empty overrides
            @Override
            public void onDownloadProgress(long arg0, long arg1, boolean arg2) {
              // do nothing
            }

            @Override
            public void onUploadProgress(long arg0, long arg1, boolean arg2) {
              // do nothing on progress
            }
          });
    } catch (ApiException ex) {
      LOGGER.error("registerAsset with LUM Failed: {}", ex.getResponseBody());
      completableFuture.completeExceptionally(ex);
    }

    return completableFuture;
  }

  private void addCatalogsToSwidBody(RegisterAssetRequest request, SWIDBody swidBody)
      throws LicenseAssetRegistrationException {
    List<SWIDBodySwCatalogs> swidCatalogs = new ArrayList<SWIDBodySwCatalogs>();

    try {
      List<MLPCatalog> catalogs =
          dataClient.getSolutionCatalogs(request.getSolutionId().toString());

      // throw error if there are no catalogs -- we should have a catalog for a model
      if (catalogs == null) {
        throw new LicenseAssetRegistrationException(
            "no catalogs found for solution", request.getSolutionId().toString());
      }
      // TODO - DO we need to include only the published catalogs?
      for (MLPCatalog catalog : catalogs) {
        SWIDBodySwCatalogs swidCatalog = new SWIDBodySwCatalogs();
        swidCatalog.setSwCatalogId(catalog.getCatalogId());
        swidCatalog.setSwCatalogType(catalog.getAccessTypeCode());
        swidCatalogs.add(swidCatalog);
      }
      // TODO possible sync issues here if catalogs change for the catalog type
      // -- but not sure if restricted / public catalogs are valid targeting value for
      // a rtu asset collection.

      swidBody.setSwCatalogs(swidCatalogs);

    } catch (RestClientResponseException ex) {
      LOGGER.error("getSolution failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseAssetRegistrationException("CDS getSolution failed", ex);
    }
  }

  private void addCDSDataToSwidBody(RegisterAssetRequest request, SWIDBody swidBody)
      throws LicenseAssetRegistrationException {
    addSwVersionToSwidTag(request, swidBody);
    addSwCategoryToSwidTag(request, swidBody);
  }

  private void addSwCategoryToSwidTag(RegisterAssetRequest request, SWIDBody swidBody)
      throws LicenseAssetRegistrationException {
    try {
      MLPSolution solution = dataClient.getSolution(request.getSolutionId().toString());
      swidBody.setSwCategory(solution.getModelTypeCode());
    } catch (RestClientResponseException ex) {
      LOGGER.error("getSolution failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseAssetRegistrationException("CDS getSolution failed", ex);
    }
  }

  private void addSwVersionToSwidTag(RegisterAssetRequest request, SWIDBody swidBody)
      throws LicenseAssetRegistrationException {
    try {
      MLPSolutionRevision revision =
          dataClient.getSolutionRevision(
              request.getSolutionId().toString(), request.getRevisionId().toString());
      swidBody.setSwVersion(revision.getVersion());
    } catch (RestClientResponseException ex) {
      LOGGER.error("getSolutionRevision failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseAssetRegistrationException("CDS getSolutionRevision failed", ex);
    }
  }
}

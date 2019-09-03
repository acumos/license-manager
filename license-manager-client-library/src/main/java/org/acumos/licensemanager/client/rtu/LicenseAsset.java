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

package org.acumos.licensemanager.client.rtu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.licensemanager.client.model.IAssignedRtuResponse;
import org.acumos.licensemanager.client.model.ICreateRtu;
import org.acumos.licensemanager.client.model.IVerifyLicenseRequest;
import org.acumos.licensemanager.client.model.LicenseVerification;
import org.acumos.licensemanager.client.model.RegisterAssetRequest;
import org.acumos.licensemanager.client.model.RegisterAssetResponse;
import org.acumos.licensemanager.exceptions.LicenseAssetRegistrationException;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.acumos.lum.handler.ApiCallback;
import org.acumos.lum.handler.ApiException;
import org.acumos.lum.handler.SwidTagApi;
import org.acumos.lum.model.BaseRequestTop;
import org.acumos.lum.model.LicenseProfile;
import org.acumos.lum.model.PutSwidTagRequest;
import org.acumos.lum.model.PutSwidTagResponse;
import org.acumos.lum.model.SWIDBodyAndCreator;
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
  private String nexusUrl;

  /** The implementation of the CDS is required to enable this library. */
  public LicenseAsset(
      final ICommonDataServiceRestClient dataServiceClient,
      final String lumServer,
      final String nexusModelRepo) {
    this.dataClient = dataServiceClient;
    this.lumServer = lumServer;
    this.nexusUrl =
        nexusModelRepo; // "http://acumos-bionic-1:30881/repository/acumos_model_maven/";
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
    if (request.getLoggedIdUser() == null) {
      throw new IllegalArgumentException("logged in user is not defined");
    }
    CompletableFuture<RegisterAssetResponse> completableFuture =
        new CompletableFuture<RegisterAssetResponse>();

    // api setup
    SwidTagApi swidTag = new SwidTagApi();
    // HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new
    // okhttp3.logging.HttpLoggingInterceptor.Logger() {
    // @Override
    // public void log(String message) {
    // // Timber.tag("OkHttp").d(message);
    // LOGGER.info(message);
    // }
    // });
    // swidTag.getApiClient().
    swidTag.getApiClient().setDebugging(true);
    swidTag.getApiClient().setWriteTimeout(300000);

    swidTag.getApiClient().setBasePath(lumServer);

    // Prepare request object -- standard for all interaction with LUM
    PutSwidTagRequest putSwidTagRequest = new PutSwidTagRequest();
    putSwidTagRequest.setRequestId(UUID.randomUUID());
    putSwidTagRequest.setRequested(OffsetDateTime.now());

    putSwidTagRequest.setUserId(request.getLoggedIdUser());

    // TODO SWIDBodySwidTagDetails -- edition, revision again, url for software
    // TODO toolkit type?

    String swTagId = request.getRevisionId().toString();
    SWIDBodyAndCreator swidBody = new SWIDBodyAndCreator();
    swidBody.setSwPersistentId(request.getSolutionId());
    swidBody.setSwTagId(swTagId);
    LicenseProfile licenseProfile = new LicenseProfile();
    // CDS based calls --- if data is not provided make calls to get category and version number
    addCDSDataToSwidBody(request, swidBody, licenseProfile);
    // add catalogs to swidbody
    addCatalogsToSwidBody(request, swidBody);
    licenseProfile.isRtuRequired(request.isRtuRequired());
    putSwidTagRequest.setLicenseProfile(licenseProfile);
    putSwidTagRequest.setSwidTag(swidBody);
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

  public final CompletableFuture<LicenseVerification> verifyRtu(final IVerifyLicenseRequest request)
      throws RightToUseException {
    return null;
  }

  public final CompletableFuture<IAssignedRtuResponse> assignUsersToRtu(final ICreateRtu request)
      throws RightToUseException {
    return null;
  }

  private void addCatalogsToSwidBody(RegisterAssetRequest request, SWIDBodyAndCreator swidBody)
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
      for (MLPCatalog catalog : catalogs) {
        SWIDBodySwCatalogs swidCatalog = new SWIDBodySwCatalogs();
        swidCatalog.setSwCatalogId(catalog.getCatalogId());
        swidCatalog.setSwCatalogType(catalog.getAccessTypeCode());
        swidCatalogs.add(swidCatalog);
      }

      swidBody.setSwCatalogs(swidCatalogs);

    } catch (RestClientResponseException ex) {
      LOGGER.error("getSolution failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseAssetRegistrationException("CDS getSolution failed", ex);
    }
  }

  private void addCDSDataToSwidBody(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody, LicenseProfile profile)
      throws LicenseAssetRegistrationException {
    addSwVersionToSwidTag(request, swidBody);
    MLPSolution solution = addSwCategoryToSwidTag(request, swidBody);
    addOwnerIfoToSwidTag(solution, request, swidBody);
    addLicenseProfileInfoToSwid(request, swidBody, profile);
  }

  private void addLicenseProfileInfoToSwid(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody, LicenseProfile profile) {
    List<MLPArtifact> artifacts =
        dataClient.getSolutionRevisionArtifacts(
            request.getSolutionId().toString(), request.getRevisionId().toString());
    if (artifacts == null) {
      return;
    }
    String artifactType = "LG";
    String fileNamePrefix = "license";
    MLPArtifact licenseProfileArtifact =
        artifacts.stream()
            .filter(
                mlpArt ->
                    mlpArt.getArtifactTypeCode().equalsIgnoreCase(artifactType)
                        && (mlpArt.getName().contains(fileNamePrefix)))
            .findFirst()
            .get();
    if (licenseProfileArtifact != null) {
      // add license profile info to LUM
      UUID uuidLicenseJsonArtifactId = UUID.fromString(licenseProfileArtifact.getArtifactId());
      swidBody.setLicenseProfileId(uuidLicenseJsonArtifactId);
      profile.setLicenseProfileId(uuidLicenseJsonArtifactId);
      // fetch json based on uri
      String uriForLicenseProfileJson = licenseProfileArtifact.getUri();
      // get json and then let's use data values
      System.out.println(uriForLicenseProfileJson);
      // request.nexusClient get json

      String licenseJsonUrl = nexusUrl + uriForLicenseProfileJson;
      OkHttpClient client = new OkHttpClient();
      Request requestLicenseJson = new Request.Builder().url(licenseJsonUrl).build();

      try (Response response = client.newCall(requestLicenseJson).execute()) {
        String licenseProfileJson = response.body().string();
        // System.out.println(licenseProfileJson);
        JsonNode jsonNode = new ObjectMapper().readTree(licenseProfileJson);
        String companyName = jsonNode.get("companyName").asText();
        swidBody.setSoftwareLicensorId(companyName);
      } catch (IOException exp) {
        LOGGER.error("license profile json failed to load");
        // throw new LicenseAssetRegistrationException("CDS + Nexus addLicenseProfileInfoToSwid
        // failed");
      }
    }

    // licenseProfile.setLicenseName(request.getLicenseName());
  }

  private void addOwnerIfoToSwidTag(
      MLPSolution solution, RegisterAssetRequest request, SWIDBodyAndCreator swidBody)
      throws LicenseAssetRegistrationException {
    try {
      // add owner userid to sw creators
      String ownerUserId = solution.getUserId(); // in federation this is admin?
      MLPUser owner = dataClient.getUser(ownerUserId);
      swidBody.setSwCreators(Arrays.asList(owner.getLoginName()));

    } catch (RestClientResponseException ex) {
      LOGGER.error("getSolution failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseAssetRegistrationException("CDS getSolution failed", ex);
    }
  }

  private MLPSolution addSwCategoryToSwidTag(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody)
      throws LicenseAssetRegistrationException {
    try {
      MLPSolution solution = dataClient.getSolution(request.getSolutionId().toString());
      swidBody.setSwCategory(solution.getModelTypeCode());
      swidBody.setSwProductName(solution.getName());
      return solution;

    } catch (RestClientResponseException ex) {
      LOGGER.error("getSolution failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseAssetRegistrationException("CDS getSolution failed", ex);
    }
  }

  private MLPSolutionRevision addSwVersionToSwidTag(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody)
      throws LicenseAssetRegistrationException {
    try {
      MLPSolutionRevision revision =
          dataClient.getSolutionRevision(
              request.getSolutionId().toString(), request.getRevisionId().toString());
      swidBody.setSwVersion(revision.getVersion());
      return revision;
    } catch (RestClientResponseException ex) {
      LOGGER.error("getSolutionRevision failed, server reports: {}", ex.getResponseBodyAsString());
      throw new LicenseAssetRegistrationException("CDS getSolutionRevision failed", ex);
    }
  }
}

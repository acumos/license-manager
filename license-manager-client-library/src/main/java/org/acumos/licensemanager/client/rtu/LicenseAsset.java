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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.licensemanager.client.model.LicenseProfileHolder;
import org.acumos.licensemanager.client.model.LicenseRtuVerification;
import org.acumos.licensemanager.client.model.RegisterAssetRequest;
import org.acumos.licensemanager.client.model.RegisterAssetResponse;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
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

  /**
   * @param dataServiceClient CDS data client for acumos used to fetch required info for LUM
   * @param lumServer LUM server such as http://127.0.0.1:2080/
   * @param nexusModelRepo Nexus repo for model artifacts
   */
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
   * @throws LicenseAssetRegistrationException when license registration fails with fetching details
   *     from CDS or accessing LUM
   * @param request information needed to register the acumos software with LUM
   */
  public final CompletableFuture<RegisterAssetResponse> register(RegisterAssetRequest request) {

    CompletableFuture<RegisterAssetResponse> registerFuture =
        new CompletableFuture<RegisterAssetResponse>();

    validateRequest(request);

    // Prepare request object -- standard for all interaction with LUM
    PutSwidTagRequest putSwidTagRequest = new PutSwidTagRequest();
    putSwidTagRequest.setUserId(request.getLoggedIdUser());

    // TODO SWIDBodySwidTagDetails -- edition, revision again, url for software
    // TODO toolkit type?

    String swTagId = request.getRevisionId().toString();
    SWIDBodyAndCreator swidBody = new SWIDBodyAndCreator();
    swidBody.setSwPersistentId(request.getSolutionId());
    swidBody.setSwTagId(swTagId);
    LicenseProfile licenseProfile = new LicenseProfile();
    putSwidTagRequest.setLicenseProfile(licenseProfile);
    putSwidTagRequest.setSwidTag(swidBody);

    // calls to CDS -- do these in parallel
    // CDS based calls --- if data is not provided make calls to get category and version number
    CompletableFuture<Object> cdsFutureSolutionData =
        addCDSDataToSwidBody(request, swidBody, licenseProfile);

    cdsFutureSolutionData.whenComplete(
        (result, exp) -> {
          if (exp != null) {
            RegisterAssetResponse resp = new RegisterAssetResponse();
            // resp.setRawResponse(arg0);
            resp.setSuccess(false);
            resp.setException(exp.getCause());
            resp.setMessage(exp.getCause().getMessage());
            resp.setRequest(request);
            registerFuture.complete(resp);
          }
        });

    cdsFutureSolutionData.thenAccept(
        (result) -> {
          callLum(swTagId, putSwidTagRequest)
              .thenAccept(
                  (lumResponse) -> {
                    RegisterAssetResponse resp = new RegisterAssetResponse();
                    // resp.setRawResponse(arg0);
                    resp.setSuccess(true);
                    resp.setLumResponse(lumResponse);
                    resp.setMessage("softwareRegistered");
                    ;
                    registerFuture.complete(resp);
                  })
              .orTimeout(1, TimeUnit.MINUTES);
        });
    return registerFuture;
  }

  private CompletableFuture<PutSwidTagResponse> callLum(
      String swTagId, PutSwidTagRequest putSwidTagRequest) {
    // api setup
    SwidTagApi swidTagApi = swidTagApiSetup();

    CompletableFuture<PutSwidTagResponse> completableFuture =
        new CompletableFuture<PutSwidTagResponse>();

    try {
      swidTagApi.putSwidTagAsync(
          swTagId,
          putSwidTagRequest,
          new ApiCallback<PutSwidTagResponse>() {

            @Override
            public void onSuccess(
                PutSwidTagResponse arg0, int arg1, Map<String, List<String>> arg2) {
              completableFuture.complete(arg0);
            }

            @Override
            public void onFailure(ApiException arg0, int arg1, Map<String, List<String>> arg2) {
              completableFuture.completeExceptionally(arg0);
            }

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

  private void validateRequest(RegisterAssetRequest request) {
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
  }

  private SwidTagApi swidTagApiSetup() {
    SwidTagApi swidTag = new SwidTagApi();
    swidTag.getApiClient().setDebugging(true);
    swidTag.getApiClient().setWriteTimeout(300000);
    swidTag.getApiClient().setBasePath(lumServer);
    return swidTag;
  }

  /**
   * This api verifies with LUM that the user is entitled to use the software If an action is
   * constrained then a denial may be enforced.
   *
   * @param request what action you want verified
   * @return License verification after async call to LUM
   * @throws RightToUseException when a right to use can not be verified
   */
  public final CompletableFuture<LicenseRtuVerification> verifyRtu(
      final VerifyLicenseRequest request) throws RightToUseException {
    return new LicenseRtuVerifier(lumServer).verifyRtu(request);
  }

  private CompletableFuture<List<SWIDBodySwCatalogs>> addCatalogsToSwidBody(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody) {
    CompletableFuture<List<SWIDBodySwCatalogs>> future =
        CompletableFuture.supplyAsync(
            () -> {
              List<SWIDBodySwCatalogs> swidCatalogs = new ArrayList<SWIDBodySwCatalogs>();
              try {
                List<MLPCatalog> catalogs =
                    dataClient.getSolutionCatalogs(request.getSolutionId().toString());

                // throw error if there are no catalogs -- we should have a catalog for a model
                if (catalogs == null || catalogs.isEmpty()) {
                  throw new LicenseAssetRegistrationException(
                      "Catalogs found for solution", request);
                }
                for (MLPCatalog catalog : catalogs) {
                  SWIDBodySwCatalogs swidCatalog = new SWIDBodySwCatalogs();
                  swidCatalog.setSwCatalogId(catalog.getCatalogId());
                  swidCatalog.setSwCatalogType(catalog.getAccessTypeCode());
                  swidCatalogs.add(swidCatalog);
                }
                swidBody.setSwCatalogs(swidCatalogs);
              } catch (RestClientResponseException ex) {
                LOGGER.error(
                    "getSolutionCatalogs failed, server reports: {}", ex.getResponseBodyAsString());
                throw new LicenseAssetRegistrationException(
                    "CDS getSolutionCatalogs failed", request, ex);
              }
              return swidCatalogs;
            });

    return future;
  }

  private CompletableFuture<Object> addCDSDataToSwidBody(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody, LicenseProfile profile) {
    CompletableFuture<Void> solutionFuture =
        addSwCategoryToSwidTag(request, swidBody)
            .thenAcceptAsync((solution) -> addOwnerIfoToSwidTag(solution, request, swidBody));

    CompletableFuture<MLPSolutionRevision> solRevisionFuture =
        addSwVersionToSwidTag(request, swidBody);

    CompletableFuture<LicenseProfileHolder> artifactFuture =
        addLicenseProfileInfoToSwid(request, swidBody, profile)
            .thenCompose(
                (holder) -> getLicenseProfileFromNexus(holder, request, swidBody, profile));
    CompletableFuture<List<SWIDBodySwCatalogs>> catalogFuture =
        addCatalogsToSwidBody(request, swidBody);
    CompletableFuture<?>[] futures =
        new CompletableFuture<?>[] {
          solRevisionFuture, artifactFuture, solutionFuture, catalogFuture
        };

    return CompletableFuture.allOf(futures)
        .thenApply(
            x -> Arrays.stream(futures).map(f -> (Object) f.join()).collect(Collectors.toList()));
  }

  private CompletableFuture<LicenseProfileHolder> addLicenseProfileInfoToSwid(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody, LicenseProfile profile) {
    return CompletableFuture.supplyAsync(
        () -> {
          LicenseProfileHolder profileHolder = new LicenseProfileHolder();
          List<MLPArtifact> artifacts;
          artifacts =
              dataClient.getSolutionRevisionArtifacts(
                  request.getSolutionId().toString(), request.getRevisionId().toString());
          if (artifacts == null || artifacts.isEmpty()) {
            throw new LicenseAssetRegistrationException("No Artifacts Found", request);
          }
          String artifactType = "LI";
          String fileNamePrefix = "license";
          MLPArtifact licenseProfileArtifact =
              artifacts.stream()
                  .filter(
                      mlpArt ->
                          mlpArt.getArtifactTypeCode().equalsIgnoreCase(artifactType)
                              && (mlpArt.getName().contains(fileNamePrefix)))
                  .findFirst()
                  .get();
          profileHolder.setArtifact(licenseProfileArtifact);
          return profileHolder;
        });
  }

  private CompletableFuture<LicenseProfileHolder> getLicenseProfileFromNexus(
      LicenseProfileHolder licenseProfileHolder,
      RegisterAssetRequest request,
      SWIDBodyAndCreator swidBody,
      LicenseProfile profile) {

    return CompletableFuture.supplyAsync(
        () -> {
          // add license profile info to LUM
          MLPArtifact licenseProfileArtifact = licenseProfileHolder.getArtifact();
          UUID uuidLicenseJsonArtifactId = UUID.fromString(licenseProfileArtifact.getArtifactId());
          swidBody.setLicenseProfileId(uuidLicenseJsonArtifactId);
          profile.setLicenseProfileId(uuidLicenseJsonArtifactId);
          // fetch json based on uri
          String uriForLicenseProfileJson = licenseProfileArtifact.getUri();
          // get json and then let's use data values
          System.out.println(uriForLicenseProfileJson);
          // request.nexusClient get json
          JsonNode jsonNode;
          String licenseJsonUrl = nexusUrl + uriForLicenseProfileJson;
          OkHttpClient client = new OkHttpClient();
          Request requestLicenseJson = new Request.Builder().url(licenseJsonUrl).build();

          try (Response response = client.newCall(requestLicenseJson).execute()) {

            if (response.isSuccessful() == false) {
              throw new LicenseAssetRegistrationException(
                  "Unable to fetch license profile", request, response);
            }
            String licenseProfileJson = response.body().string();
            // System.out.println(licenseProfileJson);
            jsonNode = new ObjectMapper().readTree(licenseProfileJson);
            JsonNode companyNameNode = jsonNode.get("companyName");
            if (companyNameNode == null) {
              throw new LicenseAssetRegistrationException(
                  "Unable to determine Company name from license profile", request);
            }
            String companyName = companyNameNode.asText();
            JsonNode rtuRequiredNode = jsonNode.get("rtuRequired");
            if (rtuRequiredNode == null) {
              throw new LicenseAssetRegistrationException(
                  "Unable to determine if RTU is required from license profile. Add rtuRequired field to license json to resolve.",
                  request);
            }
            boolean rtuRequired = rtuRequiredNode.asBoolean();
            profile.isRtuRequired(rtuRequired);
            swidBody.setSoftwareLicensorId(companyName);

          } catch (IOException exp) {
            LOGGER.error("license profile json failed to load");
            throw new LicenseAssetRegistrationException(
                "CDS + Nexus addLicenseProfileInfoToSwid" + "failed", exp);
          }
          // return jsonNode;
          licenseProfileHolder.setJson(jsonNode);
          return licenseProfileHolder;
        });
  }

  private CompletableFuture<MLPUser> addOwnerIfoToSwidTag(
      MLPSolution solution, RegisterAssetRequest request, SWIDBodyAndCreator swidBody) {

    return CompletableFuture.supplyAsync(
        () -> {
          MLPUser owner;
          try {
            // add owner userid to sw creatorsx
            String ownerUserId = solution.getUserId(); // in federation this is admin?
            owner = dataClient.getUser(ownerUserId);
            if(owner == null){
              throw new LicenseAssetRegistrationException("Owner user not found", request);
            }
            swidBody.setSwCreators(Arrays.asList(owner.getLoginName()));
          } catch (RestClientResponseException ex) {
            LOGGER.error("getUser failed, server reports: {}", ex.getResponseBodyAsString());
            throw new LicenseAssetRegistrationException("CDS getSolution failed", ex);
          }
          return owner;
        });
  }

  private CompletableFuture<MLPSolution> addSwCategoryToSwidTag(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody) {
    return CompletableFuture.supplyAsync(
        () -> {
          MLPSolution solution;
          try {
            solution = dataClient.getSolution(request.getSolutionId().toString());
            if(solution == null){
              throw new LicenseAssetRegistrationException("Solution Not found", request);
            }
            swidBody.setSwCategory(solution.getModelTypeCode());
            swidBody.setSwProductName(solution.getName());
          } catch (RestClientResponseException ex) {
            LOGGER.error("getSolution failed, server reports: {}", ex.getResponseBodyAsString());
            throw new LicenseAssetRegistrationException("CDS getSolution failed", ex);
          }
          return solution;
        });
  }

  private CompletableFuture<MLPSolutionRevision> addSwVersionToSwidTag(
      RegisterAssetRequest request, SWIDBodyAndCreator swidBody) {

    return CompletableFuture.supplyAsync(
        () -> {
          MLPSolutionRevision revision;
          try {
            revision =
                dataClient.getSolutionRevision(
                    request.getSolutionId().toString(), request.getRevisionId().toString());
            if(revision == null){
              throw new LicenseAssetRegistrationException("Solution Revision Not found", request);
            }
            swidBody.setSwVersion(revision.getVersion());

          } catch (RestClientResponseException ex) {
            LOGGER.error(
                "getSolutionRevision failed, server reports: {}", ex.getResponseBodyAsString());
            throw new LicenseAssetRegistrationException("CDS getSolutionRevision failed", ex);
          }
          return revision;
        });
  }
}

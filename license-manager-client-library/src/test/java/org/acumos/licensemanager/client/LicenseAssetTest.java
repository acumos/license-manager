package org.acumos.licensemanager.client;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.serverError;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.specto.hoverfly.junit.rule.HoverflyRule;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.licensemanager.client.model.RegisterAssetRequest;
import org.acumos.licensemanager.client.model.RegisterAssetResponse;
import org.acumos.licensemanager.client.rtu.LicenseAsset;
import org.junit.ClassRule;
import org.junit.Test;

public class LicenseAssetTest {

  private static final String LUM_SERVER = "http://lum-server.com";
  private static final String NEXUS_SERVER = "http://nexus-server.com";

  private static final String SWIDTAGID = UUID.randomUUID().toString();
  private static final String SWIDTAGID2 = UUID.randomUUID().toString();

  private class MockDatabaseClient extends CommonDataServiceRestClientMockImpl {}

  @ClassRule
  public static HoverflyRule hoverflyRule =
      HoverflyRule.inSimulationMode(
          dsl(
              service(LUM_SERVER)
                  .put("/api/v1/swid-tag/" + SWIDTAGID2)
                  .anyBody()
                  .willReturn(serverError()),
              service(LUM_SERVER)
                  .put("/api/v1/swid-tag/" + SWIDTAGID)
                  .anyBody()
                  .willReturn(success("{}", "application/json")),
              service(NEXUS_SERVER)
                  .get("/license.json")
                  // .anyBody()
                  .willReturn(
                      success(
                          "{\"rtuRequired\": true, \"companyName\": \"Company A\"}",
                          "application/json"))));

  @Test
  public void shouldRegisterAsset() {
    UUID solutionId = UUID.randomUUID();
    MockDatabaseClient mockCDSApi = new MockDatabaseClient();
    List<MLPCatalog> cats = new ArrayList<MLPCatalog>();
    cats.add(new MLPCatalog("PB", true, "Catalog", "url", "test"));
    mockCDSApi.setSolutionCatalogs(cats);
    mockArtifacts(mockCDSApi);
    LicenseAsset asset = new LicenseAsset(mockCDSApi, LUM_SERVER, NEXUS_SERVER);
    RegisterAssetRequest request = new RegisterAssetRequest();
    request.setSolutionId(solutionId);
    request.setRevisionId(SWIDTAGID);
    request.setLoggedIdUser("admin");
    try {
      CompletableFuture<RegisterAssetResponse> responseFuture = asset.register(request);
      RegisterAssetResponse response = responseFuture.get();
      assertEquals(response.getMessage(), "softwareRegistered");
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  private void mockArtifacts(MockDatabaseClient mockCDSApi) {
    List<MLPArtifact> artifacts = new ArrayList<MLPArtifact>();
    MLPArtifact artifact = new MLPArtifact();
    artifact.setArtifactTypeCode("LI");
    artifact.setArtifactId(UUID.randomUUID().toString());
    artifact.setUri("/license.json");
    artifact.setName("license.json");
    artifacts.add(artifact);

    mockCDSApi.setSolutionRevisionArtifacts(artifacts);
  }

  @Test
  public void shouldRegisterAssetVariation1() {
    UUID solutionId = UUID.randomUUID();
    MockDatabaseClient mockCDSApi = new MockDatabaseClient();
    List<MLPCatalog> cats = new ArrayList<MLPCatalog>();
    cats.add(new MLPCatalog("PB", true, "Catalog", "url", "test"));
    mockCDSApi.setSolutionCatalogs(cats);
    LicenseAsset asset = new LicenseAsset(mockCDSApi, LUM_SERVER, NEXUS_SERVER);
    RegisterAssetRequest request = new RegisterAssetRequest();
    request.setSolutionId(solutionId.toString());
    request.setRevisionId(UUID.fromString(SWIDTAGID));
    request.setLoggedIdUser("admin");
    mockArtifacts(mockCDSApi);

    try {
      CompletableFuture<RegisterAssetResponse> responseFuture = asset.register(request);
      RegisterAssetResponse response = responseFuture.get();
      assertEquals(response.getMessage(), "softwareRegistered");
      assertTrue(response.isSuccess());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldFailWithException() {
    UUID solutionId = UUID.randomUUID();
    MockDatabaseClient mockCDSApi = new MockDatabaseClient();
    List<MLPCatalog> cats = new ArrayList<MLPCatalog>();
    cats.add(new MLPCatalog("PB", true, "Catalog", "url", "test"));
    mockCDSApi.setSolutionCatalogs(cats);
    LicenseAsset asset = new LicenseAsset(mockCDSApi, LUM_SERVER, NEXUS_SERVER);
    RegisterAssetRequest request = new RegisterAssetRequest();
    request.setSolutionId(solutionId);
    request.setRevisionId(SWIDTAGID2);
    request.setLoggedIdUser("admin");
    try {
      CompletableFuture<RegisterAssetResponse> responseFuture = asset.register(request);
      RegisterAssetResponse response = responseFuture.get();
      assertFalse(response.isSuccess());
      assertEquals(solutionId.toString(), response.getSolutionId());
      assertEquals(SWIDTAGID2, response.getRevisionId());

    } catch (InterruptedException e) {
      fail(e.getMessage());
    } catch (ExecutionException e) {
      // expect this exception
      fail(e.getMessage());
    }
  }
}

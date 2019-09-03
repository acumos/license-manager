package org.acumos.licensemanager.client;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.junit.Assert.fail;

import io.specto.hoverfly.junit.rule.HoverflyRule;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.licensemanager.client.model.RegisterAssetRequest;
import org.acumos.licensemanager.client.rtu.LicenseAsset;
import org.acumos.licensemanager.exceptions.LicenseAssetRegistrationException;
import org.junit.ClassRule;
import org.junit.Test;

public class LicenseAssetTest {

  private static final String LUM_SERVER = "http://my-lum-server.com";
  private static final String SWIDTAGID = "SWIDTAGIDDUMMY";

  private class MockDatabaseClient extends CommonDataServiceRestClientMockImpl {}

  @ClassRule
  public static HoverflyRule hoverflyRule =
      HoverflyRule.inSimulationMode(
          dsl(
              service(LUM_SERVER)
                  .put("/api/v1/swid-tag/" + SWIDTAGID)
                  .willReturn(success("", "application/json"))));

  @Test
  public void shouldRegisterAsset() {
    UUID solutionId = UUID.randomUUID();
    UUID revisionId = UUID.randomUUID();
    MockDatabaseClient mockCDSApi = new MockDatabaseClient();
    List<MLPCatalog> cats = new ArrayList<MLPCatalog>();
    mockCDSApi.setSolutionCatalogs(cats);
    LicenseAsset asset = new LicenseAsset(mockCDSApi, LUM_SERVER, LUM_SERVER);
    RegisterAssetRequest request = new RegisterAssetRequest();
    request.setSolutionId(solutionId);
    request.setRevisionId(revisionId);
    request.setLoggedIdUser("admin");
    try {
      asset.register(request);
    } catch (LicenseAssetRegistrationException e) {
      fail(e.getMessage());
      e.printStackTrace();
    }

    // When
    // final ResponseEntity<String> getBookingResponse = new
    // RestTemplate().getForEntity("http://www.my-test.com/api/bookings/1", String.class);
    // // Then
    // assertThat(getBookingResponse.getStatusCode().is2xxSuccessful(), is(true));
    // assert(getBookingResponse.getBody()).equals("{\"bookingId\":\"1\"}");
  }
}

package org.acumos.licensemanager.client.model;

import org.acumos.licensemanager.exceptions.LicenseAssetRegistrationException;
import org.acumos.lum.model.PutSwidTagResponse;

// import org.acumos.lum.model.PutSwidTagResponse;

public class RegisterAssetResponse {

  private String message;
  private boolean success;
  private PutSwidTagResponse lumResponse;
  private LicenseAssetRegistrationException exception;

  public String getMessage() {
    return message;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setLumResponse(PutSwidTagResponse lumResponse) {
    this.lumResponse = lumResponse;
  }

  public PutSwidTagResponse getLumResponse() {
    return lumResponse;
  }

  public void setException(LicenseAssetRegistrationException exception) {
    this.exception = exception;
  }

  /** @return the exception */
  public LicenseAssetRegistrationException getException() {
    return exception;
  }
}

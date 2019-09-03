package org.acumos.licensemanager.client.model;

import org.acumos.lum.model.PutSwidTagResponse;

// import org.acumos.lum.model.PutSwidTagResponse;

public class RegisterAssetResponse {

  private String message;
  private boolean success;
  private PutSwidTagResponse lumResponse;
  private Throwable exception;
  private RegisterAssetRequest request;

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

  public void setException(Throwable throwable) {
    this.exception = throwable;
  }

  /** @return the exception */
  public Throwable getException() {
    return exception;
  }

  /** @return the revisionId */
  public String getRevisionId() {
    return request.getRevisionId().toString();
  }

  /** @return the solutionId */
  public String getSolutionId() {
    return request.getSolutionId().toString();
  }

  /** @return the userId */
  public String getUserId() {
    return request.getLoggedIdUser();
  }

  public void setRequest(RegisterAssetRequest request) {
    this.request = request;
  }

  /** @return the request */
  public RegisterAssetRequest getRequest() {
    return request;
  }
}

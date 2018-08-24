import {HttpTestingController} from "@angular/common/http/testing";
import {UserStatus} from "./user-status";
import {TestBed} from "@angular/core/testing";

export class HttpClientMock {

  private httpMock = TestBed.get(HttpTestingController);

  mock(): HttpTestingController {
    return this.httpMock;
  }

  finalize() {
    this.mock().verify();
  }

  mockRequest(requestUrl: string, responseBody: any): HttpClientMock {
    this.httpMock
      .expectOne(requestUrl)
      .flush(responseBody);

    return this;
  }

  mockRequestError(requestUrl: string, errorText: string): HttpClientMock {
    this.httpMock
      .expectOne(requestUrl)
      .error(new ErrorEvent(errorText));

    return this;
  }

  mockUserStatus(userStatus: UserStatus): HttpClientMock {
    this.httpMock
      .expectOne('/api/user/status')
      .flush(userStatus);

    return this;
  }
}
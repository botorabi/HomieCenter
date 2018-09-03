import {inject, TestBed} from '@angular/core/testing';

import {AppHttpInterceptorService} from './app-http-interceptor.service';
import {HttpHandler, HttpRequest, HttpXsrfTokenExtractor} from "@angular/common/http";
import {Observable} from "rxjs";
import {HttpEvent} from "@angular/common/http/src/response";


describe('AppHttpInterceptorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AppHttpInterceptorService,
        HttpXsrfTokenExtractor
      ]
    });
  });

  it('should be created', inject([AppHttpInterceptorService], (service: AppHttpInterceptorService) => {
    expect(service).toBeTruthy();
  }));


  it('should handle the CSRF header', (done) => {

    const XSRF_TOKEN_VALUE = 'My_XSRF_Token';

    class MyHttpHandler extends HttpHandler {

      handle(req: HttpRequest<any>): Observable<HttpEvent<any>> {

        expect(req.headers.has('X-XSRF-TOKEN')).toBeTruthy();
        expect(req.headers.get('X-XSRF-TOKEN')).toBe(XSRF_TOKEN_VALUE);

        return null;
      }
    }

    class MyHttpXsrfTokenExtractor extends HttpXsrfTokenExtractor {
      getToken(): string | null {
        return XSRF_TOKEN_VALUE;
      }
    }

    let next = new MyHttpHandler();
    let request = new HttpRequest<any>("GET", "http://mylink.com");
    let tokenExtractor = new MyHttpXsrfTokenExtractor();

    let service = new AppHttpInterceptorService(tokenExtractor);

    service.intercept(request, next);

    done();
  });
});

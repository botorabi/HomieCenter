import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpXsrfTokenExtractor} from "@angular/common/http";
import {AppInformationService} from "./app-information.service";
import {Observable} from "rxjs/Observable";


/**
 * Handle CSRF and session timer refreshing
 */
@Injectable()
export class AppHttpInterceptorService implements HttpInterceptor {

  constructor(
    private tokenExtractor: HttpXsrfTokenExtractor,
    private appInfo: AppInformationService
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const headerName = 'XSRF-TOKEN';
    const respHeaderName = 'X-XSRF-TOKEN';
    let token = this.tokenExtractor.getToken() as string;
    if (token !== null && !req.headers.has(headerName)) {
      req = req.clone({ headers: req.headers.set(respHeaderName, token) });
    }

    if (!req.url.endsWith("/user/status")) {
      this.appInfo.refreshLogoutTimer();
    }

    return next.handle(req);
  }
}
import {BrowserModule} from '@angular/platform-browser';
import {Injectable, NgModule} from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from "./material.module";

import {AppComponent} from './app.component';
import {
    HTTP_INTERCEPTORS,
    HttpClientModule,
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
    HttpXsrfTokenExtractor
} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {appRoutes} from "./app.routes";
import {RouterModule} from "@angular/router";
import {ViewAboutComponent} from "./view-about/view-about.component";
import {ViewHomeComponent} from "./view-home/view-home.component";
import {AppInformationService} from "./service/app-information.service";
import {ApiAuthService} from "./service/api-auth.service";
import {ViewAuthenticationComponent} from "./view-authentication/view-authentication.component";
import {Observable} from "rxjs/Observable";
import {ViewSwitchDevicesComponent} from "./view-switch-devices/view-switch-devices-component";
import {ApiDeviceService} from "./service/api-device.service";
import {ViewCameraComponent} from "./view-camera/view-camera.component";
import {SafePipe} from "./safe-pipe";
import {ViewCameraDetailsComponent} from "./view-camera-details/view-camera-details.component";
import {ViewCameraEditComponent} from "./view-camera-edit/view-camera-edit.component";


/**
 * Handle CSRF
 */
@Injectable()
export class HttpXSRFInterceptor implements HttpInterceptor {

  constructor(private tokenExtractor: HttpXsrfTokenExtractor) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
console.log("INTERCEPT HEADER: " + JSON.stringify(req));
    const headerName = 'XSRF-TOKEN';
    const respHeaderName = 'X-XSRF-TOKEN';
    let token = this.tokenExtractor.getToken() as string;
console.log("     ---> TOKEN: " + token);

    if (token !== null && !req.headers.has(headerName)) {
      req = req.clone({ headers: req.headers.set(respHeaderName, token) });
    }
console.log("     ---> INTERCEPT HEADER AFTER: " + JSON.stringify(req));
    return next.handle(req);
  }
}

@NgModule({
  declarations: [
    AppComponent,
    ViewAuthenticationComponent,
    ViewSwitchDevicesComponent,
    ViewCameraComponent,
    ViewCameraDetailsComponent,
    ViewCameraEditComponent,
    ViewAboutComponent,
    ViewHomeComponent,
    SafePipe
  ],
  imports: [
    RouterModule.forRoot(appRoutes),
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MaterialModule,
    FormsModule
  ],
  providers: [
    ApiAuthService,
    ApiDeviceService,
    AppInformationService,
    { provide: HTTP_INTERCEPTORS, useClass: HttpXSRFInterceptor, multi: true }
    ],
  bootstrap: [AppComponent]
})

export class AppModule {}

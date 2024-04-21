import { LOCALE_ID, NgModule } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import * as fr from '@angular/common/locales/fr';
import { Toast, ToastrModule } from 'ngx-toastr';

import { CloudinaryModule } from '@cloudinary/ng';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';

import { LayoutModule } from './layout/modules/layout.module';
import { AdModule } from './ad-components/modules/ad.module';
import { AuthenticationModule } from './authentication/modules/authentication.module';
import { AppRoutingModule } from './router/app-routing.module';

import { AppComponent } from './app.component';
import { HttpRequestInterceptor, httpInterceptorProviders } from './_helpers/auth.interceptor.service';


@NgModule({
  declarations: [
    AppComponent],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    FormsModule,
    ReactiveFormsModule,
    CloudinaryModule,
    NgMultiSelectDropDownModule.forRoot(),
    LayoutModule,
    AdModule,
    AuthenticationModule,
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'fr-FR'}, 
    {provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { 
  constructor() {
    registerLocaleData(fr.default);
  }
}

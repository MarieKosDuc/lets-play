import { LOCALE_ID, NgModule } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import * as fr from '@angular/common/locales/fr';

import { CloudinaryModule } from '@cloudinary/ng';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { LayoutModule } from './layout/layout.module';
import { CoreModule } from './core/core.module';
import { AuthenticationModule } from './authentication/authentication.module';
import { AppRoutingModule } from './router/app-routing.module';

import { AppComponent } from './app.component';

import { HttpRequestInterceptor, httpInterceptorProviders } from './shared/services/auth.interceptor.service';
import { EmailingService } from './shared/services/emailing.service';


@NgModule({
  declarations: [
    AppComponent],
  imports: [
    HttpClientModule,
    BrowserModule,
    ToastModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    CloudinaryModule,
    LayoutModule,
    CoreModule,
    AuthenticationModule,
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'fr-FR'}, 
    {provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true},
    MessageService, EmailingService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { 
  constructor() {
    registerLocaleData(fr.default);
  }
}

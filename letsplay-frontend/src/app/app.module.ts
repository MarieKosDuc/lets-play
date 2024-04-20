import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { CloudinaryModule } from '@cloudinary/ng';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';

import { LayoutModule } from './layout/modules/layout.module';
import { AdModule } from './ad-components/modules/ad.module';
import { AuthenticationModule } from './authentication/modules/authentication.module';
import { AppRoutingModule } from './router/app-routing.module';

import { AppComponent } from './app.component';
import { ProfileComponent } from './authentication/profile-component/profile.component';

import { httpInterceptorProviders } from './_helpers/http.interceptor.service';


@NgModule({
  declarations: [
    AppComponent,
    ProfileComponent ],
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
    AuthenticationModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }

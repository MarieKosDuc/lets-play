import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { ToastrModule } from 'ngx-toastr';

import { CloudinaryModule } from '@cloudinary/ng';
import {NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';

import { AppRoutingModule } from './router/app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { AdComponent } from './ad-components/ad/ad.component';
import { AdsListComponent } from './ad-components/ads-list/ads-list.component';
import { ScrollToTopComponent } from './scroll-to-top/scroll-to-top.component';
import { SearchComponent } from './search/search.component';
import { LoginComponent } from './authentication/login/login.component';
import { SignupComponent } from './authentication/signup/signup.component';
import { FooterComponent } from './footer/footer.component';
import { ProfileComponent } from './user/profile/profile.component';
import { AdCreateComponent } from './ad-components/ad-create/ad-create.component'; //TODO : réorganiser en plusieurs modules, importer ces modules ici


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    AdComponent,
    AdsListComponent,
    ScrollToTopComponent,
    SearchComponent,
    LoginComponent,
    SignupComponent,
    FooterComponent,
    ProfileComponent,
    AdCreateComponent  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    FormsModule,
    ReactiveFormsModule,
    CloudinaryModule,
    NgMultiSelectDropDownModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

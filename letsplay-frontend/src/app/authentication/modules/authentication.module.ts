import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from 'src/app/router/app-routing.module';

import { FormsModule } from '@angular/forms';

import { LoginComponent } from '../login-component/login.component';
import { SignupComponent } from '../signup-component/signup.component';
import { ProfileComponent } from '../profile-component/profile.component';

@NgModule({
  declarations: [
    LoginComponent,
    SignupComponent,
    ProfileComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    AppRoutingModule
  ],
  exports: [
    LoginComponent,
    SignupComponent,
    ProfileComponent
  ]
})
export class AuthenticationModule { }
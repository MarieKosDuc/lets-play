import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from 'src/app/router/app-routing.module';

import { FormsModule } from '@angular/forms';

import { ProgressSpinnerModule } from 'primeng/progressspinner';

import { LoginComponent } from './login-component/login.component';
import { SignupComponent } from './signup-component/signup.component';
import { ProfileComponent } from './profile-component/profile.component';
import { AccountVerifyComponent } from './account-verify/account-verify.component';

@NgModule({
  declarations: [
    LoginComponent,
    SignupComponent,
    ProfileComponent,
    AccountVerifyComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    AppRoutingModule,
    ProgressSpinnerModule
  ],
  exports: [
    LoginComponent,
    SignupComponent,
    ProfileComponent,
    AccountVerifyComponent
  ]
})
export class AuthenticationModule { }
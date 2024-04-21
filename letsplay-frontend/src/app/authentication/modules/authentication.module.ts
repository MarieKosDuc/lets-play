import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { ProgressSpinnerModule } from 'primeng/progressspinner';

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
    ProgressSpinnerModule
  ],
  exports: [
    LoginComponent,
    SignupComponent,
    ProfileComponent
  ]
})
export class AuthenticationModule { }
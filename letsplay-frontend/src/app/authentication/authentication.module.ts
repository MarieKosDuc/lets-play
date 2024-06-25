import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from 'src/app/router/app-routing.module';

import { FormsModule } from '@angular/forms';

import { ProgressSpinnerModule } from 'primeng/progressspinner';

import { LoginComponent } from './login-component/login.component';
import { SignupComponent } from './signup-component/signup.component';
import { ProfileComponent } from '../core/profile-component/profile.component';
import { AccountVerifyComponent } from './account-verify/account-verify.component';
import { ForgottenPasswordComponent } from './forgotten-password/forgotten-password.component';
import { AdModule } from "../core/ad.module";

@NgModule({
    declarations: [
        LoginComponent,
        SignupComponent,
        ProfileComponent,
        AccountVerifyComponent,
        ForgottenPasswordComponent,
    ],
    exports: [
        LoginComponent,
        SignupComponent,
        ProfileComponent,
        AccountVerifyComponent,
        ForgottenPasswordComponent,
    ],
    imports: [
        CommonModule,
        FormsModule,
        AppRoutingModule,
        ProgressSpinnerModule,
        AdModule
    ]
})
export class AuthenticationModule { }
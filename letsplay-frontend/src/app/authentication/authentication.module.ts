import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from 'src/app/router/app-routing.module';

import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

import { ProgressSpinnerModule } from 'primeng/progressspinner';

import { LoginComponent } from './login-component/login.component';
import { SignupComponent } from './signup-component/signup.component';
import { AccountVerifyComponent } from './account-verify-component/account-verify.component';
import { ForgottenPasswordComponent } from './forgotten-password-component/forgotten-password.component';
import { CoreModule } from "../core/core.module";

@NgModule({
    declarations: [
        LoginComponent,
        SignupComponent,
        AccountVerifyComponent,
        ForgottenPasswordComponent,
    ],
    exports: [
        LoginComponent,
        SignupComponent,
        AccountVerifyComponent,
        ForgottenPasswordComponent,
    ],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        AppRoutingModule,
        ProgressSpinnerModule,
        CoreModule
    ]
})
export class AuthenticationModule { }
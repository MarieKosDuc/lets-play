import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard, AdminAuthGuard } from '../shared/helpers/auth.guard';

import { AdsListComponent } from '../core/ads-list-component/ads-list.component';
import { SearchComponent } from '../core/search-component/search.component';
import { LoginComponent } from '../authentication/login-component/login.component';
import { SignupComponent } from '../authentication/signup-component/signup.component';
import { ProfileComponent } from '../core/profile-component/profile.component';
import { AdCreateComponent } from '../core/ad-create-component/ad-create.component';
import { AdComponent } from '../core/ad-component/ad.component';
import { AdUpdateComponent } from '../core/ad-update-component/ad-update.component';
import { LikedAdsComponent } from '../core/liked-ads-component/liked-ads.component';
import { ContactComponent } from '../core/contact-component/contact.component';
import { AccountVerifyComponent } from '../authentication/account-verify-component/account-verify.component';
import { ForgottenPasswordComponent } from '../authentication/forgotten-password-component/forgotten-password.component';
import { AboutComponent } from '../layout/about-component/about.component';
import { CguComponent } from '../layout/cgu-component/cgu.component';
import { NotFoundComponent } from '../layout/not-found-component/not-found.component';
import { AdminAdsListComponent } from '../core/admin-components/admin-ads-list/admin-ads-list.component';
import { AdminUsersListComponent } from '../core/admin-components/admin-users-list/admin-users-list.component';

const routes: Routes = [
  { path: 'home', component: AdsListComponent },
  { path: 'ad/:id', component: AdComponent },
  { path: 'search', component: SearchComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignupComponent},
  { path: 'verify/:token', component: AccountVerifyComponent },
  { path: 'resetpassword', component: ForgottenPasswordComponent },
  { path: 'resetpassword/:token', component: ForgottenPasswordComponent },
  { path: 'profile/:id', component: ProfileComponent, canActivate: [AuthGuard]},
  { path: 'create-ad', component: AdCreateComponent, canActivate: [AuthGuard] },  
  { path: 'update-ad/:id', component: AdUpdateComponent, canActivate: [AuthGuard] },
  { path: 'fav-ads', component: LikedAdsComponent, canActivate: [AuthGuard] },
  { path: 'contact/:id', component: ContactComponent, canActivate: [AuthGuard] },
  { path: 'admin/ads', component: AdminAdsListComponent, canActivate: [AdminAuthGuard] },
  { path: 'admin/users', component: AdminUsersListComponent, canActivate: [AdminAuthGuard] },
  { path: 'about', component: AboutComponent },
  { path: 'cgu', component: CguComponent },
  { path: 'not-found', component: NotFoundComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }

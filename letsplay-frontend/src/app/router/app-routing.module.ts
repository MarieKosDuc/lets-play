import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AdsListComponent } from '../core/ads-list-component/ads-list.component';
import { SearchComponent } from '../core/search-component/search.component';
import { LoginComponent } from '../authentication/login-component/login.component';
import { SignupComponent } from '../authentication/signup-component/signup.component';
import { ProfileComponent } from '../core/profile-component/profile.component';
import { AdCreateComponent } from '../core/ad-create-component/ad-create.component';
import { AdComponent } from '../core/ad-component/ad.component';
import { ContactComponent } from '../core/contact-component/contact.component';
import { AccountVerifyComponent } from '../authentication/account-verify/account-verify.component';

const routes: Routes = [
  { path: 'home', component: AdsListComponent },
  { path: 'ad/:id', component: AdComponent },
  { path: 'search', component: SearchComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignupComponent},
  { path: 'profile/:id', component: ProfileComponent},
  { path: 'my-ads', component: AdsListComponent},
  { path: 'create-ad', component: AdCreateComponent},  
  { path: 'contact/:id', component: ContactComponent},
  { path: 'verify/:token', component: AccountVerifyComponent},
  { path: '', redirectTo: '/home', pathMatch: 'full' }
]; //TODO : add auFthgard so that non identified users can only access ads and search

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }

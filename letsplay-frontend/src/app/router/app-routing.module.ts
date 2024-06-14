import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdsListComponent } from '../core/ads-list-component/ads-list.component';
import { SearchComponent } from '../core/search-component/search.component';
import { LoginComponent } from '../authentication/login-component/login.component';
import { SignupComponent } from '../authentication/signup-component/signup.component';
import { ProfileComponent } from '../authentication/profile-component/profile.component';
import { AdCreateComponent } from '../core/ad-create-component/ad-create.component';
import { AdComponent } from '../core/ad-component/ad.component';
import { ContactComponent } from '../core/contact/contact.component';

const routes: Routes = [
  { path: 'home', component: AdsListComponent },
  { path: 'ad/:id', component: AdComponent },
  { path: 'search', component: SearchComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignupComponent},
  { path: 'profile', component: ProfileComponent}, //TODO : profile/{id} to access other profiles
  { path: 'my-ads', component: AdsListComponent},
  { path: 'create-ad', component: AdCreateComponent},  
  { path: 'contact', component: ContactComponent},
  { path: '', redirectTo: '/home', pathMatch: 'full' }
]; //TODO : add authgard so that non identified users can only access ads and search

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }

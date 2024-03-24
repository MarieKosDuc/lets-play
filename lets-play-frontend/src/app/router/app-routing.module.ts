import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdsListComponent } from '../ad-components/ads-list/ads-list.component';
import { SingleAdComponent } from '../ad-components/single-ad/single-ad.component';
import { SearchComponent } from '../search/search.component';
import { LoginComponent } from '../authentication/login/login.component';
import { SignupComponent } from '../authentication/signup/signup.component';
import { ProfileComponent } from '../user/profile/profile.component';
import { AdCreateComponent } from '../ad-components/ad-create/ad-create.component';

const routes: Routes = [
  { path: 'home', component: AdsListComponent },
  { path: 'ad/:id', component: SingleAdComponent },
  { path: 'search', component: SearchComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignupComponent},
  { path: 'profile', component: ProfileComponent}, //TODO : profile/{id} to access other profiles
  { path: 'create-ad', component: AdCreateComponent},  
  { path: '', redirectTo: '/home', pathMatch: 'full' }
]; //TODO : add authgard so that non identified users can only access ads and search

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

import { MultiSelectModule } from 'primeng/multiselect';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ToastModule } from 'primeng/toast';
import { SelectButtonModule } from 'primeng/selectbutton';
import { DropdownModule } from 'primeng/dropdown';

import { AdComponent } from './ad-component/ad.component';
import { AdsListComponent } from './ads-list-component/ads-list.component';
import { SearchComponent } from './search-component/search.component';
import { AdCreateComponent } from './ad-create-component/ad-create.component';
import { AdUpdateComponent } from './ad-update-component/ad-update.component';
import { AdRecapComponent } from './ad-recap-component/ad-recap.component';
import { ContactComponent } from './contact-component/contact.component';
import { LikedAdsComponent } from './liked-ads-component/liked-ads.component';
import { ProfileComponent } from './profile-component/profile.component';

import { AdminAdsListComponent } from './admin-components/admin-ads-list/admin-ads-list.component';
import { AdminUsersListComponent } from './admin-components/admin-users-list/admin-users-list.component';

@NgModule({
  declarations: [
    AdComponent,
    AdsListComponent,
    SearchComponent,
    AdCreateComponent,
    AdUpdateComponent,
    AdRecapComponent,
    ContactComponent,
    LikedAdsComponent,
    ProfileComponent,
    AdminAdsListComponent,
    AdminUsersListComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    ProgressSpinnerModule,
    MultiSelectModule,
    ToastModule,
    SelectButtonModule,
    DropdownModule
  ],
  exports: [
    AdComponent,
    AdsListComponent,
    SearchComponent,
    AdCreateComponent,
    AdUpdateComponent,
    AdRecapComponent,
    LikedAdsComponent,
    ProfileComponent,
    AdminAdsListComponent,
    AdminUsersListComponent
  ]
})
export class CoreModule { }
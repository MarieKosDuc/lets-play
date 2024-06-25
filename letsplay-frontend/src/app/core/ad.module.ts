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
import { AdRecapComponent } from './ad-recap/ad-recap.component';
import { ContactComponent } from './contact-component/contact.component';

@NgModule({
  declarations: [
    AdComponent,
    AdsListComponent,
    SearchComponent,
    AdCreateComponent,
    AdRecapComponent,
    ContactComponent
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
    AdRecapComponent
  ]
})
export class AdModule { }
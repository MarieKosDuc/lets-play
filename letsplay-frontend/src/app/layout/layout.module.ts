import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { CloudinaryModule } from '@cloudinary/ng';

import { ToastModule } from 'primeng/toast';
import { ScrollTopModule } from 'primeng/scrolltop';
import { MenuModule } from 'primeng/menu';
import { ButtonModule } from 'primeng/button';

import { HeaderComponent } from './header-component/header.component';
import { FooterComponent } from './footer-component/footer.component';
import { AboutComponent } from './about-component/about.component';
import { CguComponent } from './cgu-component/cgu.component';
import { NotFoundComponent } from './not-found-component/not-found.component';

@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    AboutComponent,
    CguComponent,
    NotFoundComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    CloudinaryModule,
    ToastModule,
    ScrollTopModule,
    MenuModule,
    ButtonModule
  ],
  exports: [
    HeaderComponent,
    FooterComponent,
    AboutComponent,
    CguComponent,
    NotFoundComponent
  ]
})
export class LayoutModule { }

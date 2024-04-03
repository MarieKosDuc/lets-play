import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { CloudinaryModule } from '@cloudinary/ng';

import { HeaderComponent } from '../header-component/header.component';
import { FooterComponent } from '../footer-component/footer.component';
import { ScrollToTopComponent } from '../scroll-to-top-component/scroll-to-top.component';

@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    ScrollToTopComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    CloudinaryModule
  ],
  exports: [
    HeaderComponent,
    FooterComponent,
    ScrollToTopComponent
  ]
})
export class LayoutModule { }

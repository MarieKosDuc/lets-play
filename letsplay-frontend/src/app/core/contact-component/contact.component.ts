import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Ad } from '../models/ad.model';
import { AdService } from '../ad.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css',
})
export class ContactComponent {
  protected ad?: Ad;
  protected contactForm!: FormGroup;

  constructor(private adService: AdService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.adService
      .getAdById(this.route.snapshot.params['id'])
      .subscribe((ad: Ad) => {
        this.ad = ad;
      });

    this.contactForm = new FormGroup({
      message: new FormControl('', [Validators.required]),
    });
  }

  onSubmit(): void {
    if (this.contactForm.valid) {
      // A implémenter
    }
  }
}

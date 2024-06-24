import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { MessageService } from 'primeng/api';

import { Ad } from '../models/ad.model';
import { AdService } from '../ad.service';
import { EmailingService } from '../../shared/services/emailing.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css',
})
export class ContactComponent {
  protected ad?: Ad;
  protected contactForm!: FormGroup;
  protected messageSent: boolean = false;

  constructor(private adService: AdService, private route: ActivatedRoute, private emailService: EmailingService, private messageService: MessageService) {}

  ngOnInit(): void {
    this.adService
      .getAdById(this.route.snapshot.params['id'])
      .subscribe((ad: Ad) => {
        this.ad = ad;
        console.log('Ad:', this.ad)
      });

    this.contactForm = new FormGroup({
      textMessage: new FormControl('', [Validators.required]),
    });
  }

  onSubmit(): void {
    if (this.contactForm.valid) {
      this.emailService.sendEmail(this.ad!, this.contactForm.value.textMessage).subscribe(
        (response) => {
          console.log('Email sent successfully:', response);
          this.messageSent = true;
        },
        (error) => {
          console.error('Error sending email:', error);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de l\'envoi du mail' });
        }
      );

    }
  }

  backToAd(): void {
    window.history.back();
  }
}

import { Component, Input, OnInit } from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import { AdService } from '../services/ad.service';
import { StorageService } from 'src/app/_services/storage.service';
import { Ad } from '../models/ad.model';
import { User } from 'src/app/authentication/models/user.model';

import { MessageService } from 'primeng/api';
import { response } from 'express';

@Component({
  selector: 'app-ad-recap',
  templateUrl: './ad-recap.component.html',
  styleUrls: ['./ad-recap.component.css'],
  providers: [MessageService]
})
export class AdRecapComponent implements OnInit {
  @Input() ad!: Ad;
  protected user?: User;
  protected visibleToast: boolean = false;

  constructor(private router: Router,
    private adService: AdService,
    private storageService: StorageService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.user = this.storageService.getUser();
  }

  isUserAuthor(): boolean {
    return this.user?.username === this.ad.postedBy;
  }

  showConfirm() {
    if (!this.visibleToast) {
        this.messageService.add({ key: 'confirm', sticky: true, severity: 'info', summary: 'Es-tu sûr de vouloir supprimer cette annonce ?' });
        this.visibleToast = true;
    }
}

onConfirm() {
  console.log('add deletion : ', this.ad.id)
    this.deleteAd();
    this.messageService.clear('confirm');
    this.visibleToast = false;
}

onReject() {
    this.messageService.clear('confirm');
    this.visibleToast = false;
}

  goToAd() {
    this.router.navigateByUrl(`ad/${this.ad.id}`);
  }

  deleteAd() {
    this.adService.deleteAd(this.ad.id).subscribe(() => {
      (response: any) => {
        console.log(response);
      }
      this.messageService.add({ severity: 'success', summary: 'Annonce supprimée', detail: 'Ton annonce a bien été supprimée' });
    });
    this.router.navigate(['/my-ads']);

  }

}

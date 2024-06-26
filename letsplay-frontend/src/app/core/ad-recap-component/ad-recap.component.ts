import { Component, Input, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { AdService } from '../ad.service';
import { AuthStorageService } from 'src/app/shared/services/storage.service';
import { Ad } from '../models/ad.model';
import { User } from 'src/app/authentication/models/user.model';

import { MessageService } from 'primeng/api';
import { response } from 'express';

@Component({
  selector: 'app-ad-recap',
  templateUrl: './ad-recap.component.html',
  styleUrls: ['./ad-recap.component.css'],
})
export class AdRecapComponent implements OnInit {
  @Input() ad!: Ad;
  protected user?: User;
  protected visibleToast: boolean = false;
  protected starIcon: string = 'pi pi-star-fill';

  constructor(
    private router: Router,
    private adService: AdService,
    private authStorageService: AuthStorageService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.user = this.authStorageService.getUser();
  }

  protected isUserAuthor(): boolean {
    return this.user?.id === this.ad.postedById;
  }

  protected onFavoriteClick(event: Event): void {
    if (this.user) {
    this.adService.adOrRemoveFavorite(this.user.id, this.ad.id).subscribe(() => {
      this.starIcon = this.starIcon === 'pi pi-star-fill' ? 'pi pi-star' : 'pi pi-star-fill';

      this.messageService.add({
        severity: 'success',
        summary: 'Favoris',
        detail: this.starIcon === 'pi pi-star-fill' ? 'Annonce ajoutée aux favoris' : 'Annonce retirée des favoris',
      });
      window.location.reload();
    }, (error) => {
      this.messageService.add({
        severity: 'error',
        summary: 'Erreur',
        detail: 'Une erreur est survenue',
      });
    });
  }
  }

  protected showConfirm() {
    if (!this.visibleToast) {
      this.messageService.add({
        key: 'confirm',
        sticky: true,
        severity: 'info',
        summary: 'Es-tu sûr de vouloir supprimer cette annonce ?',
      });
      this.visibleToast = true;
    }
  }

  protected onConfirm() {
    console.log('add deletion, clicked on "Oui" for : ', this.ad.id);
    this.deleteAd();
    this.messageService.clear('confirm');
    this.visibleToast = false;
  }

  protected onReject() {
    this.messageService.clear('confirm');
    this.visibleToast = false;
  }

  protected goToAd() {
    this.router.navigateByUrl(`ad/${this.ad.id}`);
  }

  protected deleteAd() {
    this.adService.deleteAd(this.ad.id).subscribe(() => {
      console.log("service ok");
      this.messageService.add({
        severity: 'success',
        summary: 'Annonce supprimée',
        detail: 'Ton annonce a bien été supprimée',
      });

      this.router.navigate(['/home']);
    });
  }

  protected updateAd(): void {
    this.router.navigateByUrl(`update-ad/${this.ad.id}`);
  }
}

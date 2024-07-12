import { Component, Input, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { AdService } from '../services/ad.service';
import { AuthStorageService } from 'src/app/shared/services/auth.storage.service';
import { Ad } from '../models/ad.model';
import { User } from 'src/app/authentication/models/user.model';
import { RolesEnum } from 'src/app/shared/enums/rolesEnum';
import { AdminService } from '../services/admin.service';

import { MessageService } from 'primeng/api';

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
    private messageService: MessageService,
    private adminService: AdminService
  ) {}

  ngOnInit(): void {
    this.user = this.authStorageService.getUser();
  }

  protected isUserAuthor(): boolean {
    return this.user?.id === this.ad.postedById;
  }

  protected isAdmin(): boolean {
    return this.user?.roles?.includes(RolesEnum.ADMIN) ?? false;
  }

  protected onFavoriteClick(event: Event): void {
    if (this.user) {
      this.adService.adOrRemoveFavorite(this.user.id, this.ad.id).subscribe({
        next: (response) => {
          this.authStorageService.toggleFavoriteInStorage(this.ad.id);

          this.starIcon =
            this.starIcon === 'pi pi-star-fill'
              ? 'pi pi-star'
              : 'pi pi-star-fill';

          this.messageService.add({
            severity: 'success',
            summary: 'Favoris',
            detail:
              this.starIcon === 'pi pi-star-fill'
                ? 'Annonce ajoutée aux favoris'
                : 'Annonce retirée des favoris',
          });
          window.location.reload();
        },
        error: (error) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erreur',
            detail: 'Une erreur est survenue',
          });
        },
      });
    }
  }

  protected showConfirm() {
    if (!this.visibleToast) {
      this.messageService.add({
        key: 'confirm',
        sticky: true,
        severity: 'info',
        summary: 'Es-tu sûr.e de vouloir supprimer cette annonce ?',
      });
      this.visibleToast = true;
    }
  }

  protected onConfirm() {
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
    if (this.isAdmin()) {
      this.deleteAdByAdmin();
    } else {
      this.deleteAdByUser();
    }
  }

  protected deleteAdByUser() {
    this.adService.deleteAd(this.ad.id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Annonce supprimée',
          detail: 'Ton annonce a bien été supprimée',
        });

        this.router.navigate(['/home']);
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: "Une erreur est survenue lors de la suppression de l'annonce",
        });
      },
    });
  }

  protected deleteAdByAdmin() {
    this.adminService.deleteAd(this.ad.id).subscribe({
      next: (response) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Annonce supprimée',
          detail: "L'annonce a bien été supprimée",
        });
        this.router.navigate(['/admin/ads']);
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail:
            "Une erreur est survenue lors de la suppression de l'annonce. Merci de réessayer",
        });
      },
    });
  }

  protected updateAd(): void {
    this.router.navigateByUrl(`update-ad/${this.ad.id}`);
  }
}

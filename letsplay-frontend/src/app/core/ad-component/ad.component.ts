import { Component, OnInit, Input } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { MessageService } from 'primeng/api';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { AuthStorageService } from '../../shared/services/auth.storage.service';

import { User } from 'src/app/authentication/models/user.model';
import { MusicStylesEnum } from '../enums/musicStylesEnum';
import { LocationsEnum } from '../enums/locationsEnum';
import { RolesEnum } from 'src/app/shared/enums/rolesEnum';

@Component({
  selector: 'app-ad',
  templateUrl: './ad.component.html',
  styleUrls: ['./ad.component.css'],
})
export class AdComponent implements OnInit {
  @Input()
  public ad!: Ad;

  @Input()
  protected truncated = true;

  protected connectedUser!: User;
  protected isUserConnected: boolean = false;
  protected isSingleAd!: boolean;
  protected isFavorite!: boolean;

  protected starIcon: string = 'pi pi-star';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private adService: AdService,
    private authStorageService: AuthStorageService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.authStorageService.user$.subscribe({
      next: (user) => {
        this.connectedUser = user;
        if (Object.keys(this.connectedUser).length !== 0) {
          this.isUserConnected = true;
          this.isFavorite =
            this.connectedUser.likedAds?.includes(this.ad.id) ?? false;
          this.starIcon = this.isFavorite ? 'pi pi-star-fill' : 'pi pi-star';
        }
      },
    });

    const currentRoute = this.route.snapshot.routeConfig?.path;
    if (currentRoute === 'home' || currentRoute === 'search') {
      this.isSingleAd = false;
      this.ad.description = this.truncateText(this.ad.description);
    }

    if (currentRoute === 'ad/:id') {
      this.isSingleAd = true;
      const adId = this.route.snapshot.params['id'];
      this.adService.getAdById(adId).subscribe({
        next: (ad: Ad) => {
          this.ad = ad;
          this.transformAd(this.ad);
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

  protected isAdmin(): boolean {
    return this.connectedUser.roles?.includes(RolesEnum.ADMIN) ?? false;
  }

  protected onFavoriteClick(event: Event): void {
    this.adService
      .adOrRemoveFavorite(this.connectedUser.id, this.ad.id)
      .subscribe({
        next: (response) => {
          this.starIcon =
            this.starIcon === 'pi pi-star' ? 'pi pi-star-fill' : 'pi pi-star';
          this.authStorageService.toggleFavoriteInStorage(this.ad.id);
          this.messageService.add({
            severity: 'success',
            summary: 'Favoris',
            detail:
              this.starIcon === 'pi pi-star-fill'
                ? 'Annonce ajoutée aux favoris'
                : 'Annonce retirée des favoris',
          });
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

  protected truncateText(text: string): string {
    const words = text.split(' ');
    if (words.length <= 30) {
      return text;
    }
    const truncatedText = words.slice(0, 30);
    return truncatedText.join(' ') + '...';
  }

  protected goToAd(): void {
    this.router.navigateByUrl(`ad/${this.ad.id}`);
  }

  protected isAuthorUser(): boolean {
    return this.connectedUser.id === this.ad.postedById;
  }

  protected onProfileClick(event: Event): void {
    if (Object.keys(this.connectedUser).length !== 0) {
      this.router.navigateByUrl(`profile/${this.ad.postedById}`);
    } else {
      this.messageService.add({
        severity: 'error',
        summary: 'Erreur',
        detail: 'Tu dois être connecté pour voir le profil de cet utilisateur',
      });
    }
  }

  protected updateAd(): void {
    this.router.navigateByUrl(`update-ad/${this.ad.id}`);
  }

  protected contact() {
    if (Object.keys(this.connectedUser).length === 0) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erreur',
        detail: 'Tu dois être connecté pour répondre à une annonce',
      });
    } else {
      this.router.navigate(['/contact', this.ad?.id]);
    }
  }

  private transformStyles(styles: string[]): string[] {
    return styles.map(
      (style) => MusicStylesEnum[style as keyof typeof MusicStylesEnum] || style
    );
  }

  private transformLocation(location: string): string {
    return LocationsEnum[location as keyof typeof LocationsEnum] || location;
  }

  private transformAd(ad: Ad): void {
    ad.styles = this.transformStyles(ad.styles);
    ad.location = this.transformLocation(ad.location);
  }
}
